package com.eventmgmt.ui;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;
import com.eventmgmt.service.EventService;
import com.eventmgmt.service.RegistrationService;
import com.eventmgmt.service.SessionService;
import com.eventmgmt.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class StudentMyRegistrationsPanel extends JPanel {
    private final RegistrationService registrationService;
    private final EventService eventService;
    private final SessionService sessionService;
    private final Participant currentStudent;
    private final DataChangeListener onDataChangedCallback;

    // UI Components
    private JTable regTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalRegistrations;

    public StudentMyRegistrationsPanel(RegistrationService registrationService,
                                       EventService eventService,
                                       SessionService sessionService,
                                       Participant currentStudent,
                                       DataChangeListener onDataChangedCallback) {
        this.registrationService = registrationService;
        this.eventService = eventService;
        this.sessionService = sessionService;
        this.currentStudent = currentStudent;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        loadMyRegistrations();
    }

    
    private void initComponents() {
       
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createTitledBorder("Student Registration Status"));

        lblTotalRegistrations = new JLabel("Loading your registrations...");
        lblTotalRegistrations.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalRegistrations.setBorder(new EmptyBorder(6, 8, 6, 8));
        topBar.add(lblTotalRegistrations, BorderLayout.WEST);

        JPanel btnTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMyRegistrations();
            }
        });
        btnTop.add(btnRefresh);
        topBar.add(btnTop, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        
        String[] cols = {"Registration ID", "Event Name", "Fee (Rs.)", "Payment Status", "Enrollment Status", "Registered Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };

        regTable = new JTable(tableModel);
        regTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        regTable.setRowHeight(24);
        regTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(regTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Enrolled Events & Payment Verification Status"));
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        JButton btnViewPayment = new JButton("View Payment & Enrollment Details");
        btnViewPayment.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnViewPayment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewPaymentDetails();
            }
        });
        bottomBar.add(btnViewPayment);

        JButton btnViewSchedule = new JButton("View Event Schedule");
        btnViewSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewSchedule();
            }
        });
        bottomBar.add(btnViewSchedule);

        JButton btnCancel = new JButton("Cancel My Registration");
        btnCancel.setForeground(new Color(180, 0, 0));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelRegistration();
            }
        });
        bottomBar.add(btnCancel);

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadMyRegistrations() {
        tableModel.setRowCount(0);
        List<Registration> registrationList = registrationService.getRegistrationsForParticipant(currentStudent.getId());
        
        for (int i = 0; i < registrationList.size(); i++) {
            Registration reg = registrationList.get(i);
            String feeText = reg.getFeeAmount() > 0 ? String.format("Rs. %.2f", reg.getFeeAmount()) : "FREE";
            String enrollmentStatus;
            
            if (reg.isPaymentConfirmed()) {
                enrollmentStatus = "SUCCESSFULLY REGISTERED (CONFIRMED)";
            } else if (reg.isPaymentPending()) {
                enrollmentStatus = "PAYMENT PENDING (Awaiting Admin Confirmation)";
            } else if ("REJECTED".equalsIgnoreCase(reg.getPaymentStatus())) {
                enrollmentStatus = "PAYMENT REJECTED";
            } else {
                enrollmentStatus = reg.getStatus();
            }

            tableModel.addRow(new Object[]{
                    reg.getRegistrationId(),
                    reg.getEventName() != null ? reg.getEventName() : reg.getEventId(),
                    feeText,
                    reg.getPaymentStatus(),
                    enrollmentStatus,
                    DateTimeUtil.formatDate(reg.getRegistrationDate())
            });
        }

        lblTotalRegistrations.setText("Student: " + currentStudent.getName() + " (" + currentStudent.getId() + ") | Total Registrations: " + registrationList.size());
    }

    private void handleViewPaymentDetails() {
        int selected = regTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select an event from your table first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) tableModel.getValueAt(selected, 0);
        List<Registration> registrationList = registrationService.getRegistrationsForParticipant(currentStudent.getId());
        Registration targetRegistration = null;
        
        for (int i = 0; i < registrationList.size(); i++) {
            if (registrationList.get(i).getRegistrationId().equalsIgnoreCase(regId)) {
                targetRegistration = registrationList.get(i);
                break;
            }
        }

        if (targetRegistration == null) {
            return;
        }

        Event event = eventService.getEventById(targetRegistration.getEventId());

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         EVENT REGISTRATION & PAYMENT RECEIPT       \n");
        sb.append("====================================================\n\n");
        sb.append("Registration ID   : ").append(targetRegistration.getRegistrationId()).append("\n");
        sb.append("Event Name        : ").append(targetRegistration.getEventName() != null ? targetRegistration.getEventName() : targetRegistration.getEventId()).append("\n");
        
        if (event != null) {
            sb.append("Event