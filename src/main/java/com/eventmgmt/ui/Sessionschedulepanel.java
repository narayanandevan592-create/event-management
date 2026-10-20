package com.eventmgmt.ui;
import com.eventmgmt.exception.InvalidSessionTimeException;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Session;
import com.eventmgmt.service.EventService;
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


public class SessionSchedulePanel extends JPanel {
    private final SessionService sessionService;
    private final EventService eventService;
    private final DataChangeListener onDataChangedCallback;

    
    private JComboBox<EventComboItem> comboEvents;

   
    private JTable sessionTable;
    private DefaultTableModel tableModel;

   
    private JTextField txtSessionId;
    private JTextField txtTitle;
    private JTextField txtDate;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextField txtVenue;

    public SessionSchedulePanel(SessionService sessionService,
                                EventService eventService,
                                DataChangeListener onDataChangedCallback) {
        this.sessionService = sessionService;
        this.eventService = eventService;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        refreshEventsDropdown();
    }

    
    private void initComponents() {
        // --- TOP: Event Selection Header ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Associated Event (Composition)"));

        topPanel.add(new JLabel("Event:"));
        comboEvents = new JComboBox<EventComboItem>();
        comboEvents.setPreferredSize(new Dimension(320, 26));
        comboEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEventSelectionChanged();
            }
        });
        topPanel.add(comboEvents);

        JButton btnViewSchedule = new JButton("View Full Day Schedule");
        btnViewSchedule.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnViewSchedule.setBackground(new Color(230, 245, 230));
        btnViewSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewSchedule();
            }
        });
        topPanel.add(btnViewSchedule);

        add(topPanel, BorderLayout.NORTH);

        
        String[] cols = {"Session ID", "Session Title", "Date", "Start Time", "End Time", "Duration (mins)", "Venue / Room"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setRowHeight(24);
        sessionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        sessionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    populateFormFromSelectedRow();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Event Sessions Schedule"));

        
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setPreferredSize(new Dimension(360, 0));
        formContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Session Details & Time Validation", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("Session ID:"), gbc);
        txtSessionId = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtSessionId, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("Session Title:"), gbc);
        txtTitle = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtTitle, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        txtDate = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtDate, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("Start Time (HH:mm):"), gbc);
        txtStartTime = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtStartTime, gbc);

       
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("End Time (HH:mm):"), gbc);
        txtEndTime = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtEndTime, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(new JLabel("Room / Venue:"), gbc);
        txtVenue = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtVenue, gbc);

        formContainer.add(formFields, BorderLayout.CENTER);

       
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        buttonPanel.setBorder(new EmptyBorder(8, 4, 8, 4));

        JButton btnAdd = new JButton("Create Session");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddSession();
            }
        });
        buttonPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Update Session");
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateSession();
            }
        });
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete Session");
        btnDelete.setForeground(new Color(180, 0, 0));
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteSession();
            }
        });
        buttonPanel.add(btnDelete);

        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        buttonPanel.add(btnClear);

        JButton btnGenId = new JButton("Generate ID");
        btnGenId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSessionId.setText("SES-" + (int)(Math.random() * 900 + 100));
            }
        });
        buttonPanel.add(btnGenId);

        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, formContainer);
        splitPane.setResizeWeight(0.65);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshEventsDropdown() {
        EventComboItem currentSelection = (EventComboItem) comboEvents.getSelectedItem();
        comboEvents.removeAllItems();

        List<Event> events = eventService.getAllEvents();
        EventComboItem toReselect = null;
        
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            EventComboItem item = new EventComboItem(e.getEventId(), e.getEventName(), e.getDate());
            comboEvents.addItem(item);
            
            if (currentSelection != null && currentSelection.eventId.equals(e.getEventId())) {
                toReselect = item;
            }
        }

        if (toReselect != null) {
            comboEvents.setSelectedItem(toReselect);
        } else if (comboEvents.getItemCount() > 0) {
            comboEvents.setSelectedIndex(0);
        }
        onEventSelectionChanged();
    }

    private void onEventSelectionChanged() {
        EventComboItem selected = (EventComboItem) comboEvents.getSelectedItem();
        if (selected != null) {
            txtDate.setText(DateTimeUtil.formatDate(selected.date));
            loadSessionsForEvent(selected.eventId);
        } else {
            tableModel.setRowCount(0);
        }
    }

    private void loadSessionsForEvent(String eventId) {
        tableModel.setRowCount(0);
        List<Session> sessions = sessionService.getSessionsByEventId(eventId);
        
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            tableModel.addRow(new Object[]{
                    s.getSessionId(),
                    s.getTitle(),
                    DateTimeUtil.formatDate(s.getDate()),
                    DateTimeUtil.formatTime(s.getStartTime()),
                    DateTimeUtil.formatTime(s.getEndTime()),
                    s.getDurationMinutes(),
                    s.getVenue()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int selected = sessionTable.getSelectedRow();
        if (selected >= 0) {
            String sessionId = (String) tableModel.getValueAt(selected, 0);
            Session session = sessionService.getSessionById(sessionId);
            
            if (session != null) {
                txtSessionId.setText(session.getSessionId());
                txtTitle.setText(session.getTitle());
                txtDate.setText(DateTimeUtil.formatDate(session.getDate()));
                txtStartTime.setText(DateTimeUtil.formatTime(session.getStartTime()));
                txtEndTime.setText(DateTimeUtil.formatTime(session.getEndTime()));
                txtVenue.setText(session.getVenue());
            }
        }
    }

    private void handleAddSession() {
        EventComboItem selectedEvent = (EventComboItem) comboEvents.getSelectedItem();
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Session session = extractSessionFromForm(selectedEvent.eventId);
            sessionService.createSession(session);
            
            JOptionPane.showMessageDialog(this,
                    "Session '" + session.getTitle() + "' scheduled successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            loadSessionsForEvent(selectedEvent.eventId);
            clearForm();
            notifyDataChanged();
        } catch (InvalidSessionTimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Session Timing Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateSession() {
        EventComboItem selectedEvent = (EventComboItem) comboEvents.getSelectedItem();
        if (selectedEvent == null) {
            return;
        }

        try {
            Session session = extractSessionFromForm(selectedEvent.eventId);
            boolean updated = sessionService.updateSession(session);
            
            if (updated) {
                JOptionPane.showMessageDialog(this, "Session updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSessionsForEvent(selectedEvent.eventId);
                notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Session not found for update.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (InvalidSessionTimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Session Timing Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSession() {
        int selected = sessionTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session from the table to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sessionId = (String) tableModel.getValueAt(selected, 0);
        String title = (String) tableModel.getValueAt(selected, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete session '" + title + "' (" + sessionId + ")?",
                "Confirm Session Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                sessionService.deleteSession(sessionId);
                JOptionPane.showMessageDialog(this, "Session deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                
                EventComboItem selectedEvent = (EventComboItem) comboEvents.getSelectedItem();
                if (selectedEvent != null) {
                    loadSessionsForEvent(selectedEvent.eventId);
                }
                clearForm();
                notifyDataChanged();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting session: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewSchedule() {
        EventComboItem selectedEvent = (EventComboItem) comboEvents.getSelectedItem();
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event to view its schedule.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String scheduleText = sessionService.getFormattedSchedule(selectedEvent.eventId);
        JTextArea area = new JTextArea(scheduleText, 18, 55);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Chronological Schedule: " + selectedEvent.title, JOptionPane.PLAIN_MESSAGE);
    }

    private Session extractSessionFromForm(String eventId) {
        String id = txtSessionId.getText().trim();
        String title = txtTitle.getText().trim();
        String dateStr = txtDate.getText().trim();
        String startStr = txtStartTime.getText().trim();
        String endStr = txtEndTime.getText().trim();
        String venue = txtVenue.getText().trim();

        if (dateStr.isEmpty()) {
            throw new ValidationException("Session Date is required.");
        }
        if (startStr.isEmpty()) {
            throw new ValidationException("Start Time (HH:mm) is required.");
        }
        if (endStr.isEmpty()) {
            throw new ValidationException("End Time (HH:mm) is required.");
        }

        if (!DateTimeUtil.isValidDate(dateStr)) {
            throw new ValidationException("Invalid Date format: '" + dateStr + "'. Use YYYY-MM-DD.");
        }
        if (!DateTimeUtil.isValidTime(startStr)) {
            throw new ValidationException("Invalid Start Time format: '" + startStr + "'. Use 24-hr format HH:mm.");
        }
        if (!DateTimeUtil.isValidTime(endStr)) {
            throw new ValidationException("Invalid End Time format: '" + endStr + "'. Use 24-hr format HH:mm.");
        }

        return new Session(id, eventId, title, dateStr, startStr, endStr, venue);
    }

    private void clearForm() {
        txtSessionId.setText("");
        txtTitle.setText("");
        txtStartTime.setText("");
        txtEndTime.setText("");
        txtVenue.setText("");
        sessionTable.clearSelection();
    }

    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.onDataChanged();
        }
    }


    public static class EventComboItem {
        public final String eventId;
        public final String title;
        public final String date;

        public EventComboItem(String eventId, String title, String date) {
            this.eventId = eventId;
            this.title = title;
            this.date = date;
        }

        @Override
        public String toString() {
            return title + " (" + eventId + ")";
        }
    }
}