package com.eventmgmt.ui;
import com.eventmgmt.exception.DuplicateRegistrationException;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;
import com.eventmgmt.service.EventService;
import com.eventmgmt.service.ParticipantService;
import com.eventmgmt.service.RegistrationService;
import com.eventmgmt.util.DateTimeUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class ParticipantRegistrationPanel extends JPanel {
    private final ParticipantService participantService;
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final DataChangeListener onDataChangedCallback;

    
    private JTable participantTable;
    private DefaultTableModel participantTableModel;
    private JTextField txtPartId;
    private JTextField txtPartName;
    private JTextField txtPartEmail;
    private JTextField txtPartPhone;
    private JTextField txtSearchParticipant;

    
    private JComboBox<EventComboItem> comboEvents;
    private JComboBox<ParticipantComboItem> comboParticipants;
    private JComboBox<EventComboItem> comboFilterEvent;
    private JTable registrationTable;
    private DefaultTableModel registrationTableModel;

   
    private JTable pendingPaymentTable;
    private DefaultTableModel pendingPaymentTableModel;
    private JLabel lblPendingCount;

    public ParticipantRegistrationPanel(ParticipantService participantService,
                                        EventService eventService,
                                        RegistrationService registrationService,
                                        DataChangeListener onDataChangedCallback) {
        this.participantService = participantService;
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Manage Participants", createParticipantsPanel());
        subTabs.addTab("Event Registrations", createRegistrationsPanel());
        subTabs.addTab("Offline Payment Approvals", createPaymentApprovalsPanel());

        add(subTabs, BorderLayout.CENTER);

        refreshAllData();
    }

   
    private JPanel createParticipantsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Search Bar Panel
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search Participants"));
        searchBar.add(new JLabel("Search (Name/Email/ID):"));
        
        txtSearchParticipant = new JTextField(20);
        searchBar.add(txtSearchParticipant);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performParticipantSearch();
            }
        });
        searchBar.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchParticipant.setText("");
                loadParticipantsToTable();
            }
        });
        searchBar.add(btnReset);
        panel.add(searchBar, BorderLayout.NORTH);

        String[] cols = {"Participant ID", "Full Name", "Email Address", "Phone Number"};
        participantTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        participantTable = new JTable(participantTableModel);
        participantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        participantTable.setRowHeight(24);

        participantTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    populateParticipantForm();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(participantTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Participants"));

        
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setPreferredSize(new Dimension(360, 0));
        formContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Participant Information", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        fields.add(new JLabel("Participant ID:"), gbc);
        txtPartId = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        fields.add(txtPartId, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        fields.add(new JLabel("Full Name:"), gbc);
        txtPartName = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        fields.add(txtPartName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        fields.add(new JLabel("Email Address:"), gbc);
        txtPartEmail = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        fields.add(txtPartEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        fields.add(new JLabel("Phone Number:"), gbc);
        txtPartPhone = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        fields.add(txtPartPhone, gbc);

        formContainer.add(fields, BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        btnPanel.setBorder(new EmptyBorder(8, 4, 8, 4));

        JButton btnAdd = new JButton("Add Participant");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddParticipant();
            }
        });
        btnPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Update Info");
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateParticipant();
            }
        });
        btnPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete Participant");
        btnDelete.setForeground(new Color(180, 0, 0));
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteParticipant();
            }
        });
        btnPanel.add(btnDelete);

        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearParticipantForm();
            }
        });
        btnPanel.add(btnClear);

        JButton btnViewEvents = new JButton("View Enrolled Events");
        btnViewEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewParticipantEnrolledEvents();
            }
        });
        btnPanel.add(btnViewEvents);

        JButton btnGenId = new JButton("Generate ID");
        btnGenId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPartId.setText("PAR-" + (int)(Math.random() * 900 + 100));
            }
        });
        btnPanel.add(btnGenId);

        formContainer.add(btnPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, formContainer);
        split.setResizeWeight(0.65);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    
    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Top: Association Registration Box
        JPanel regBox = new JPanel(new GridBagLayout());
        regBox.setBorder(BorderFactory.createTitledBorder("Register Participant for Event (Association)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        regBox.add(new JLabel("Select Event:"), gbc);
        comboEvents = new JComboBox<EventComboItem>();
        gbc.gridx = 1; gbc.weightx = 0.4;
        regBox.add(comboEvents, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        regBox.add(new JLabel("Select Participant:"), gbc);
        comboParticipants = new JComboBox<ParticipantComboItem>();
        gbc.gridx = 3; gbc.weightx = 0.4;
        regBox.add(comboParticipants, gbc);

        JButton btnRegister = new JButton("Register Now");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRegister.setBackground(new Color(220, 240, 255));
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0.2;
        regBox.add(btnRegister, gbc);

        panel.add(regBox, BorderLayout.NORTH);

       
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.add(new JLabel("Filter Registrations by Event:"));
        comboFilterEvent = new JComboBox<EventComboItem>();
        comboFilterEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyRegistrationFilter();
            }
        });
        filterBar.add(comboFilterEvent);

        JButton btnShowAll = new JButton("Show All Registrations");
        btnShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboFilterEvent.setSelectedIndex(0);
                loadRegistrationsToTable(null);
            }
        });
        filterBar.add(btnShowAll);

        JButton btnConfirmPayment = new JButton("Confirm Offline Payment");
        btnConfirmPayment.setBackground(new Color(220, 245, 220));
        btnConfirmPayment.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConfirmPayment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConfirmPaymentFromReg();
            }
        });
        filterBar.add(btnConfirmPayment);

        JButton btnCancelReg = new JButton("Cancel Selected Registration");
        btnCancelReg.setForeground(new Color(180, 0, 0));
        btnCancelReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelRegistration();
            }
        });
        filterBar.add(btnCancelReg);

        centerPanel.add(filterBar, BorderLayout.NORTH);

        String[] regCols = {"Registration ID", "Event Name", "Participant Name", "Email", "Fee (Rs.)", "Payment Status", "Registration Date", "Enrollment Status"};
        registrationTableModel = new DefaultTableModel(regCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        registrationTable = new JTable(registrationTableModel);
        registrationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationTable.setRowHeight(24);

        JScrollPane regScrollPane = new JScrollPane(registrationTable);
        centerPanel.add(regScrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentApprovalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

       
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(254, 243, 199)); 
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        lblPendingCount = new JLabel("Offline Payment Verification: Loading pending payments...");
        lblPendingCount.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPendingCount.setForeground(new Color(146, 64, 14));
        headerPanel.add(lblPendingCount, BorderLayout.CENTER);

        JLabel lblInstruction = new JLabel("Students pay cash/desk fee offline at venue. Click 'Accept & Confirm' after receiving payment to enroll.");
        lblInstruction.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInstruction.setForeground(new Color(180, 83, 9));
        headerPanel.add(lblInstruction, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Pending Payments Table
        String[] pendingCols = {"Registration ID", "Student Name", "Email", "Event Name", "Fee Amount (Rs.)", "Payment Note / Venue", "Date Submitted"};
        pendingPaymentTableModel = new DefaultTableModel(pendingCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        pendingPaymentTable = new JTable(pendingPaymentTableModel);
        pendingPaymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingPaymentTable.setRowHeight(26);
        pendingPaymentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane tableScroll = new JScrollPane(pendingPaymentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Pending Offline Payments Awaiting Administrator Approval"));
        panel.add(tableScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        JButton btnAccept = UIUtils.createStyledButton("Accept & Confirm Payment (Enroll Student)", new Color(34, 197, 94), Color.WHITE, 12);
        btnAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAcceptPendingPayment();
            }
        });
        actionPanel.add(btnAccept);

        JButton btnReject = new JButton("Reject Payment Claim");
        btnReject.setForeground(new Color(220, 38, 38));
        btnReject.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnReject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRejectPendingPayment();
            }
        });
        actionPanel.add(btnReject);

        JButton btnRefresh = new JButton("Refresh Pending List");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPendingPaymentsToTable();
            }
        });
        actionPanel.add(btnRefresh);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

   
    public void refreshAllData() {
        loadParticipantsToTable();
        refreshDropdowns();
        loadRegistrationsToTable(null);
        loadPendingPaymentsToTable();
    }

    public void loadPendingPaymentsToTable() {
        if (pendingPaymentTableModel == null) {
            return;
        }
        pendingPaymentTableModel.setRowCount(0);
        List<Registration> pending = registrationService.getPendingPayments();
        
        for (int i = 0; i < pending.size(); i++) {
            Registration r = pending.get(i);
            pendingPaymentTableModel.addRow(new Object[]{
                    r.getRegistrationId(),
                    r.getParticipantName() != null ? r.getParticipantName() : r.getParticipantId(),
                    r.getParticipantEmail() != null ? r.getParticipantEmail() : "N/A",
                    r.getEventName() != null ? r.getEventName() : r.getEventId(),
                    String.format("Rs. %.2f", r.getFeeAmount()),
                    r.getPaymentNotes().isEmpty() ? "Offline Payment at Venue Desk" : r.getPaymentNotes(),
                    DateTimeUtil.formatDate(r.getRegistrationDate())
            });
        }

        if (lblPendingCount != null) {
            if (pending.isEmpty()) {
                lblPendingCount.setText("Offline Payment Verification: No pending payments at this time.");
                lblPendingCount.setForeground(new Color(22, 101, 52));
            } else {
                lblPendingCount.setText("ATTENTION: " + pending.size() + " Student Offline Payment(s) Awaiting Administrator Acceptance!");
                lblPendingCount.setForeground(new Color(185, 28, 28));
            }
        }
    }

    private void handleAcceptPendingPayment() {
        int selected = pendingPaymentTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pending payment record from the table to accept.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) pendingPaymentTableModel.getValueAt(selected, 0);
        String studentName = (String) pendingPaymentTableModel.getValueAt(selected, 1);
        String eventName = (String) pendingPaymentTableModel.getValueAt(selected, 3);
        String fee = (String) pendingPaymentTableModel.getValueAt(selected, 4);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm that offline payment of " + fee + " has been received at the venue desk from:\n" +
                "Student: " + studentName + "\n" +
                "Event:   " + eventName + "\n" +
                "Reg ID:  " + regId + "\n\n" +
                "Accept payment and officially ENROLL this student?",
                "Confirm Offline Payment Receipt",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                registrationService.confirmPayment(regId);
                JOptionPane.showMessageDialog(this,
                        "PAYMENT CONFIRMED!\n\n" +
                        studentName + " is now officially enrolled in '" + eventName + "'.",
                        "Student Enrolled", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error accepting payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRejectPendingPayment() {
        int selected = pendingPaymentTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pending payment record to reject.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) pendingPaymentTableModel.getValueAt(selected, 0);
        String studentName = (String) pendingPaymentTableModel.getValueAt(selected, 1);
        String eventName = (String) pendingPaymentTableModel.getValueAt(selected, 3);

        String reason = JOptionPane.showInputDialog(
                this,
                "Enter reason for rejecting payment claim for " + studentName + " in '" + eventName + "':",
                "Payment not received at venue desk"
        );

        if (reason != null) {
            try {
                registrationService.rejectPayment(regId, reason);
                JOptionPane.showMessageDialog(this, "Payment claim marked as REJECTED.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error rejecting payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleConfirmPaymentFromReg() {
        int selected = registrationTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) registrationTableModel.getValueAt(selected, 0);
        String eventName = (String) registrationTableModel.getValueAt(selected, 1);
        String studentName = (String) registrationTableModel.getValueAt(selected, 2);
        String payStatus = (String) registrationTableModel.getValueAt(selected, 5);

        if ("CONFIRMED".equalsIgnoreCase(payStatus)) {
            JOptionPane.showMessageDialog(this, "Payment for this registration is already confirmed!", "Already Confirmed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Accept offline payment and confirm registration for:\n" +
                "Student: " + studentName + "\n" +
                "Event:   " + eventName + "\n" +
                "Reg ID:  " + regId + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                registrationService.confirmPayment(regId);
                JOptionPane.showMessageDialog(this, "Payment accepted and registration confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadParticipantsToTable() {
        participantTableModel.setRowCount(0);
        List<Participant> participants = participantService.getAllParticipants();
        
        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            participantTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getEmail(), p.getPhone()
            });
        }
    }

    private void refreshDropdowns() {
        comboEvents.removeAllItems();
        comboFilterEvent.removeAllItems();
        comboFilterEvent.addItem(new EventComboItem(null, "--- All Events ---"));

        List<Event> events = eventService.getAllEvents();
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            EventComboItem item = new EventComboItem(e.getEventId(), e.getEventName());
            comboEvents.addItem(item);
            comboFilterEvent.addItem(item);
        }

        comboParticipants.removeAllItems();
        List<Participant> participants = participantService.getAllParticipants();
        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            comboParticipants.addItem(new ParticipantComboItem(p.getId(), p.getName(), p.getEmail()));
        }
    }

    private void loadRegistrationsToTable(String filterEventId) {
        registrationTableModel.setRowCount(0);
        List<Registration> list;
        
        if (filterEventId == null || filterEventId.isEmpty()) {
            list = registrationService.getAllRegistrations();
        } else {
            list = registrationService.getRegistrationsForEvent(filterEventId);
        }

        for (int i = 0; i < list.size(); i++) {
            Registration r = list.get(i);
            String feeText = r.getFeeAmount() > 0 ? String.format("Rs. %.2f", r.getFeeAmount()) : "FREE";
            
            registrationTableModel.addRow(new Object[]{
                    r.getRegistrationId(),
                    r.getEventName() != null ? r.getEventName() : r.getEventId(),
                    r.getParticipantName() != null ? r.getParticipantName() : r.getParticipantId(),
                    r.getParticipantEmail() != null ? r.getParticipantEmail() : "N/A",
                    feeText,
                    r.getPaymentStatus(),
                    DateTimeUtil.formatDate(r.getRegistrationDate()),
                    r.getStatus()
            });
        }
    }

    private void populateParticipantForm() {
        int selected = participantTable.getSelectedRow();
        if (selected >= 0) {
            txtPartId.setText((String) participantTableModel.getValueAt(selected, 0));
            txtPartName.setText((String) participantTableModel.getValueAt(selected, 1));
            txtPartEmail.setText((String) participantTableModel.getValueAt(selected, 2));
            txtPartPhone.setText((String) participantTableModel.getValueAt(selected, 3));
        }
    }

    private void handleAddParticipant() {
        try {
            Participant participant = new Participant(
                    txtPartId.getText().trim(),
                    txtPartName.getText().trim(),
                    txtPartEmail.getText().trim(),
                    txtPartPhone.getText().trim()
            );
            participantService.addParticipant(participant);
            
            JOptionPane.showMessageDialog(this, "Participant '" + participant.getName() + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData();
            clearParticipantForm();
            notifyDataChanged();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding participant: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateParticipant() {
        try {
            Participant participant = new Participant(
                    txtPartId.getText().trim(),
                    txtPartName.getText().trim(),
                    txtPartEmail.getText().trim(),
                    txtPartPhone.getText().trim()
            );
            boolean updated = participantService.updateParticipant(participant);
            
            if (updated) {
                JOptionPane.showMessageDialog(this, "Participant updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Participant not found for update.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating participant: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteParticipant() {
        int selected = participantTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a participant to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String partId = (String) participantTableModel.getValueAt(selected, 0);
        String name = (String) participantTableModel.getValueAt(selected, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete participant '" + name + "' (" + partId + ")?\n" +
                "This will also cancel all their active event registrations.",
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                participantService.deleteParticipant(partId);
                JOptionPane.showMessageDialog(this, "Participant deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                clearParticipantForm();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting participant: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewParticipantEnrolledEvents() {
        int selected = participantTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a participant to view their events.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String partId = (String) participantTableModel.getValueAt(selected, 0);
        String name = (String) participantTableModel.getValueAt(selected, 1);

        List<Registration> regs = registrationService.getRegistrationsForParticipant(partId);
        if (regs.isEmpty()) {
            JOptionPane.showMessageDialog(this, name + " is not currently registered for any events.", "No Registrations", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Events Registered for ").append(name).append(" (").append(partId).append("):\n\n");
        
        for (int i = 0; i < regs.size(); i++) {
            Registration r = regs.get(i);
            sb.append(i + 1).append(". ")
              .append(r.getEventName() != null ? r.getEventName() : r.getEventId())
              .append(" | Date: ").append(DateTimeUtil.formatDate(r.getRegistrationDate()))
              .append(" | Status: ").append(r.getStatus())
              .append("\n");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Enrolled Events for " + name, JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRegister() {
        EventComboItem selectedEvent = (EventComboItem) comboEvents.getSelectedItem();
        ParticipantComboItem selectedPart = (ParticipantComboItem) comboParticipants.getSelectedItem();

        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event to register for.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedPart == null) {
            JOptionPane.showMessageDialog(this, "Please select a participant.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Registration reg = registrationService.registerParticipant(selectedEvent.eventId, selectedPart.participantId);
            JOptionPane.showMessageDialog(
                    this,
                    "Registration SUCCESSFUL!\n" +
                    "Registration ID: " + reg.getRegistrationId() + "\n" +
                    "Participant: " + selectedPart.name + "\n" +
                    "Event: " + selectedEvent.title,
                    "Registration Confirmed", 
                    JOptionPane.INFORMATION_MESSAGE
            );
            applyRegistrationFilter();
            notifyDataChanged();
        } catch (DuplicateRegistrationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() + "\nDuplicate registrations for the same participant and event are prevented.",
                    "Duplicate Registration Prevented", 
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelRegistration() {
        int selected = registrationTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = (String) registrationTableModel.getValueAt(selected, 0);
        String eventName = (String) registrationTableModel.getValueAt(selected, 1);
        String partName = (String) registrationTableModel.getValueAt(selected, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the registration #" + regId + " for " + partName + " in '" + eventName + "'?",
                "Confirm Cancellation", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                registrationService.cancelRegistration(regId);
                JOptionPane.showMessageDialog(this, "Registration cancelled successfully.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                applyRegistrationFilter();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cancelling registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyRegistrationFilter() {
        EventComboItem item = (EventComboItem) comboFilterEvent.getSelectedItem();
        String filterId = (item != null) ? item.eventId : null;
        loadRegistrationsToTable(filterId);
    }

    private void performParticipantSearch() {
        String keyword = txtSearchParticipant.getText().trim();
        List<Participant> matches = participantService.searchParticipants(keyword);
        
        participantTableModel.setRowCount(0);
        for (int i = 0; i < matches.size(); i++) {
            Participant p = matches.get(i);
            participantTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getEmail(), p.getPhone()
            });
        }
    }

    private void clearParticipantForm() {
        txtPartId.setText("");
        txtPartName.setText("");
        txtPartEmail.setText("");
        txtPartPhone.setText("");
        participantTable.clearSelection();
    }

    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.onDataChanged();
        }
    }

   

    public static class EventComboItem {
        public final String eventId;
        public final String title;

        public EventComboItem(String eventId, String title) {
            this.eventId = eventId;
            this.title = title;
        }

        @Override
        public String toString() {
            return (eventId == null) ? title : title + " (" + eventId + ")";
        }
    }

    public static class ParticipantComboItem {
        public final String participantId;
        public final String name;
        public final String email;

        public ParticipantComboItem(String participantId, String name, String email) {
            this.participantId = participantId;
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return name + " (" + email + ")";
        }
    }
}