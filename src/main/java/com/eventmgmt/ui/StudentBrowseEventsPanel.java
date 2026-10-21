package com.eventmgmt.ui;
import com.eventmgmt.exception.DuplicateRegistrationException;
import com.eventmgmt.exception.ValidationException;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class StudentBrowseEventsPanel extends JPanel {
    private final EventService eventService;
    private final SessionService sessionService;
    private final RegistrationService registrationService;
    private final Participant currentStudent;
    private final DataChangeListener onDataChangedCallback;

   
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JTextArea txtEventDetails;
    private JButton btnRegister;
    private String selectedEventId;

    public StudentBrowseEventsPanel(EventService eventService,
                                    SessionService sessionService,
                                    RegistrationService registrationService,
                                    Participant currentStudent,
                                    DataChangeListener onDataChangedCallback) {
        this.eventService = eventService;
        this.sessionService = sessionService;
        this.registrationService = registrationService;
        this.currentStudent = currentStudent;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        loadEvents();
    }

    
    private void initComponents() {
        // --- TOP: Search Bar Panel ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search Available Events"));
        searchBar.add(new JLabel("Search Event:"));
        
        txtSearch = new JTextField(22);
        searchBar.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchBar.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadEvents();
            }
        });
        searchBar.add(btnReset);

        add(searchBar, BorderLayout.NORTH);

       
        String[] cols = {"Event ID", "Event Name", "Fee (Rs.)", "Date (YYYY-MM-DD)", "Venue", "Organizer", "Sessions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };
        
        eventTable = new JTable(tableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.setRowHeight(24);
        eventTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        eventTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    showSelectedEventDetails();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(eventTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Upcoming Events Catalog"));

        
        JPanel detailsPanel = new JPanel(new BorderLayout(8, 8));
        detailsPanel.setPreferredSize(new Dimension(380, 0));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Event Details & Schedule", TitledBorder.LEFT, TitledBorder.TOP));

        txtEventDetails = new JTextArea();
        txtEventDetails.setEditable(false);
        txtEventDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtEventDetails.setText("Select an event from the table to view its full details and session schedule.");
        
        JScrollPane detailsScroll = new JScrollPane(txtEventDetails);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);

       
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        actionPanel.setBorder(new EmptyBorder(6, 4, 6, 4));

        btnRegister = UIUtils.createStyledButton("Register for Selected Event", new Color(46, 125, 50), Color.WHITE, 13);
        btnRegister.setEnabled(false);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterForEvent();
            }
        });
        actionPanel.add(btnRegister);

        JButton btnViewSchedule = new JButton("View Full Day Schedule");
        btnViewSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewFullSchedule();
            }
        });
        actionPanel.add(btnViewSchedule);

        detailsPanel.add(actionPanel, BorderLayout.SOUTH);

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailsPanel);
        splitPane.setResizeWeight(0.62);
        add(splitPane, BorderLayout.CENTER);
    }

    public void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events = eventService.getAllEvents();
        
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            String feeText = event.isPaid() ? String.format("Rs. %.2f", event.getRegistrationFee()) : "FREE";
            
            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    feeText,
                    DateTimeUtil.formatDate(event.getDate()),
                    event.getVenue(),
                    event.getOrganizerName(),
                    event.getSessions().size()
            });
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        List<Event> results = eventService.searchEvents(keyword);
        
        tableModel.setRowCount(0);
        for (int i = 0; i < results.size(); i++) {
            Event event = results.get(i);
            String feeText = event.isPaid() ? String.format("Rs. %.2f", event.getRegistrationFee()) : "FREE";
            
            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    feeText,
                    DateTimeUtil.formatDate(event.getDate()),
                    event.getVenue(),
                    event.getOrganizerName(),
                    event.getSessions().size()
            });
        }
    }

    private void showSelectedEventDetails() {
        int selected = eventTable.getSelectedRow();
        if (selected < 0) {
            selectedEventId = null;
            btnRegister.setEnabled(false);
            txtEventDetails.setText("Select an event from the table to view its full details and session schedule.");
            return;
        }

        selectedEventId = (String) tableModel.getValueAt(selected, 0);
        Event event = eventService.getEventById(selectedEventId);
        if (event == null) {
            btnRegister.setEnabled(false);
            return;
        }

        
        boolean isAlreadyRegistered = registrationService.isParticipantRegistered(selectedEventId, currentStudent.getId());

        if (isAlreadyRegistered) {
            btnRegister.setText("Already Registered / Enrolled");
            UIUtils.styleButton(btnRegister, new Color(120, 144, 156), Color.WHITE, 13);
            btnRegister.setEnabled(false);
        } else {
            if (event.isPaid()) {
                btnRegister.setText("Pay Offline & Register (Rs. " + String.format("%.2f", event.getRegistrationFee()) + ")");
                UIUtils.styleButton(btnRegister, new Color(21, 101, 192), Color.WHITE, 13); // Royal Blue
            } else {
                btnRegister.setText("Register for this Event (FREE)");
                UIUtils.styleButton(btnRegister, new Color(46, 125, 50), Color.WHITE, 13); // Green
            }
            btnRegister.setEnabled(true);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("           EVENT SPECIFICATION           \n");
        sb.append("=========================================\n\n");
        sb.append("Event ID   : ").append(event.getEventId()).append("\n");
        sb.append("Title      : ").append(event.getEventName()).append("\n");
        sb.append("Reg Fee    : ").append(event.isPaid() ? String.format("Rs. %.2f (Offline Payment at Venue Desk)", event.getRegistrationFee()) : "FREE (Direct Enrollment)").append("\n");
        sb.append("Date       : ").append(DateTimeUtil.formatDate(event.getDate())).append("\n");
        sb.append("Venue      : ").append(event.getVenue()).append("\n");
        sb.append("Organizer  : ").append(event.getOrganizerName()).append("\n\n");
        sb.append("Description:\n").append(event.getDescription().isEmpty() ? "No description provided." : event.getDescription()).append("\n\n");

        sb.append("-----------------------------------------\n");
        sb.append("SESSIONS (").append(event.getSessions().size()).append(" Scheduled):\n");
        sb.append("-----------------------------------------\n");
        
        if (event.getSessions().isEmpty()) {
            sb.append("No sessions scheduled yet for this event.\n");
        } else {
            for (int i = 0; i < event.getSessions().size(); i++) {
                Session session = event.getSessions().get(i);
                sb.append(String.format(" %d. %s\n    Time: %s - %s @ %s\n",
                        (i + 1), session.getTitle(),
                        DateTimeUtil.formatTime(session.getStartTime()),
                        DateTimeUtil.formatTime(session.getEndTime()),
                        session.getVenue()));
            }
        }
        sb.append("=========================================\n");

        txtEventDetails.setText(sb.toString());
        txtEventDetails.setCaretPosition(0);
    }

    private void handleRegisterForEvent() {
        if (selectedEventId == null) {
            JOptionPane.showMessageDialog(this, "Please select an event first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Event event = eventService.getEventById(selectedEventId);
        if (event == null) {
            return;
        }

        if (event.isPaid()) {
            
            JPanel payPanel = new JPanel(new GridLayout(0, 1, 4, 4));
            payPanel.add(new JLabel("<html><b>Event:</b> " + event.getEventName() + " (" + event.getEventId() + ")</html>"));
            payPanel.add(new JLabel("<html><b>Registration Fee:</b> <font color='blue'>Rs. " + String.format("%.2f", event.getRegistrationFee()) + "</font></html>"));
            payPanel.add(new JLabel("<html><b>Payment Mode:</b> OFFLINE (Cash / Venue Desk Payment)</html>"));
            payPanel.add(new JLabel("<html><b>Payment Venue:</b> " + event.getVenue() + " [Registration Desk]</html>"));
            payPanel.add(new JLabel(" "));
            payPanel.add(new JLabel("Payment Reference / Receipt Note (Optional):"));
            
            JTextField txtNote = new JTextField("Paid cash at venue registration desk");
            payPanel.add(txtNote);
            payPanel.add(new JLabel("<html><font color='gray'><i>* After submitting, the Administrator will verify your payment at the venue<br>and accept your enrollment.</i></font></html>"));

            int res = JOptionPane.showConfirmDialog(
                    this, 
                    payPanel, 
                    "Offline Event Payment — " + event.getEventName(),
                    JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE
            );

            if (res == JOptionPane.OK_OPTION) {
                try {
                    String note = txtNote.getText().trim();
                    Registration registration = registrationService.registerParticipantWithPayment(selectedEventId, currentStudent.getId(), note);
                    
                    JOptionPane.showMessageDialog(
                            this,
                            "OFFLINE PAYMENT CLAIM SUBMITTED!\n\n" +
                            "Registration ID: " + registration.getRegistrationId() + "\n" +
                            "Event: " + event.getEventName() + "\n" +
                            "Fee Amount: Rs. " + String.format("%.2f", event.getRegistrationFee()) + "\n" +
                            "Payment Status: PENDING_APPROVAL\n\n" +
                            "Next Steps:\n" +
                            "1. Pay the registration fee at the venue desk (" + event.getVenue() + ").\n" +
                            "2. The Administrator will review and accept your payment.\n" +
                            "3. Once accepted, your enrollment will show as 'CONFIRMED'.",
                            "Registration Submitted (Pending Approval)", 
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    showSelectedEventDetails();
                    if (onDataChangedCallback != null) {
                        onDataChangedCallback.onDataChanged();
                    }
                } catch (DuplicateRegistrationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Registration Prevented", JOptionPane.WARNING_MESSAGE);
                } catch (ValidationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Free event direct registration workflow
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Register " + currentStudent.getName() + " for FREE event:\n" +
                    "Event: " + event.getEventName() + " (" + DateTimeUtil.formatDate(event.getDate()) + ")\n" +
                    "Venue: " + event.getVenue() + "\n\nConfirm direct registration?",
                    "Confirm Free Registration",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Registration registration = registrationService.registerParticipant(selectedEventId, currentStudent.getId());
                    
                    JOptionPane.showMessageDialog(
                            this,
                            "REGISTRATION SUCCESSFUL!\n\n" +
                            "Registration ID: " + registration.getRegistrationId() + "\n" +
                            "Event: " + event.getEventName() + "\n" +
                            "Fee: FREE\n" +
                            "Enrollment Status: CONFIRMED",
                            "Enrolled Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    showSelectedEventDetails();
                    if (onDataChangedCallback != null) {
                        onDataChangedCallback.onDataChanged();
                    }
                } catch (DuplicateRegistrationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Registration Prevented", JOptionPane.WARNING_MESSAGE);
                } catch (ValidationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleViewFullSchedule() {
        if (selectedEventId == null) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String schedule = sessionService.getFormattedSchedule(selectedEventId);
        JTextArea area = new JTextArea(schedule, 18, 55);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Day Schedule", JOptionPane.PLAIN_MESSAGE);
    }
}
