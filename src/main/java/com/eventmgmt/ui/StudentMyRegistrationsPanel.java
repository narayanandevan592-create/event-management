package com.eventmgmt.ui;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;
import com.eventmgmt.model.Session;
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
            sb.append("Event Date        : ").append(DateTimeUtil.formatDate(event.getDate())).append("\n");
            sb.append("Venue             : ").append(event.getVenue()).append("\n");
            sb.append("Organizer         : ").append(event.getOrganizerName()).append("\n");
        }
        sb.append("Participant Name  : ").append(currentStudent.getName()).append("\n");
        sb.append("Participant Email : ").append(currentStudent.getEmail()).append("\n");
        sb.append("Phone Number      : ").append(currentStudent.getPhone()).append("\n");
        sb.append("Registered Date   : ").append(DateTimeUtil.formatDate(targetRegistration.getRegistrationDate())).append("\n");
        sb.append("Registration Fee  : ").append(targetRegistration.getFeeAmount() > 0 ? String.format("Rs. %.2f", targetRegistration.getFeeAmount()) : "FREE").append("\n");
        sb.append("Payment Mode      : ").append(targetRegistration.getPaymentMode()).append("\n");
        sb.append("Payment Status    : ").append(targetRegistration.getPaymentStatus()).append("\n");
        sb.append("Enrollment Status : ").append(targetRegistration.getStatus()).append("\n");
        if (targetRegistration.getPaymentNotes() != null && !targetRegistration.getPaymentNotes().trim().isEmpty()) {
            sb.append("Payment Notes     : ").append(targetRegistration.getPaymentNotes()).append("\n");
        }
        sb.append("====================================================\n");

        JTextArea textArea = new JTextArea(sb.toString(), 18, 45);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Registration & Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewSchedule() {
        int selected = regTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table first.", "Notice", JOptionPane.WARNING_MESSAGE);
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
        if (event == null) {
            JOptionPane.showMessageDialog(this, "Event details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Session> sessions = sessionService.getSessionsByEventId(event.getEventId());
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("               EVENT SCHEDULE & SESSIONS             \n");
        sb.append("====================================================\n\n");
        sb.append("Event: ").append(event.getEventName()).append(" (").append(event.getEventId()).append(")\n");
        sb.append("Date : ").append(DateTimeUtil.formatDate(event.getDate())).append("\n");
        sb.append("Venue: ").append(event.getVenue()).append("\n\n");
        sb.append("Sessions:\n");
        if (sessions == null || sessions.isEmpty()) {
            sb.append("  (No sessions scheduled yet for this event)\n");
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                Session s = sessions.get(i);
                sb.append(String.format("  %d. %s [%s - %s @ %s] (%d mins)\n",
                        i + 1, s.getTitle(), s.getStartTime(), s.getEndTime(), s.getVenue(), s.getDurationMinutes()));
            }
        }
        sb.append("====================================================\n");

        JTextArea textArea = new JTextArea(sb.toString(), 16, 45);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Event Schedule: " + event.getEventName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCancelRegistration() {
        int selected = regTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table to cancel.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) tableModel.getValueAt(selected, 0);
        String eventName = (String) tableModel.getValueAt(selected, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel your registration for '" + eventName + "' (" + regId + ")?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean cancelled = registrationService.cancelRegistration(regId);
                if (cancelled) {
                    JOptionPane.showMessageDialog(this, "Registration cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMyRegistrations();
                    if (onDataChangedCallback != null) {
                        onDataChangedCallback.onDataChanged();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel registration.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cancelling registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
