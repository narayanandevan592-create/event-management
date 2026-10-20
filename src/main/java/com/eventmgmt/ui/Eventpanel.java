package com.eventmgmt.ui;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.service.EventService;
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


public class EventPanel extends JPanel {
    private final EventService eventService;
    private final DataChangeListener onDataChangedCallback;

    
    private JTable eventTable;
    private DefaultTableModel tableModel;

    
    private JTextField txtId;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JTextField txtDate;
    private JTextField txtVenue;
    private JTextField txtOrganizer;

   
    private JComboBox<String> comboEventType;
    private JTextField txtFee;

   
    private JTextField txtSearch;

    public EventPanel(EventService eventService, DataChangeListener onDataChangedCallback) {
        this.eventService = eventService;
        this.onDataChangedCallback = onDataChangedCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        loadEventsToTable();
    }

    
    private void initComponents() {
        // --- TOP: Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Events"));
        searchPanel.add(new JLabel("Keyword:"));
        
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchPanel.add(btnSearch);

        JButton btnResetSearch = new JButton("Reset");
        btnResetSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadEventsToTable();
            }
        });
        searchPanel.add(btnResetSearch);

        add(searchPanel, BorderLayout.NORTH);

        
        String[] columnNames = {"Event ID", "Event Name", "Fee (Rs.)", "Date (YYYY-MM-DD)", "Venue", "Organizer", "Sessions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells read-only
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
                    populateFormFromSelectedRow();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(eventTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Events List"));

       
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setPreferredSize(new Dimension(380, 0));
        formContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Event Details & Actions", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        
       
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Event ID:"), gbc);
        txtId = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtId, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Event Name:"), gbc);
        txtName = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtName, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Event Type:"), gbc);
        comboEventType = new JComboBox<String>(new String[]{"Free Event", "Paid Event"});
        comboEventType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isPaid = "Paid Event".equals(comboEventType.getSelectedItem());
                txtFee.setEnabled(isPaid);
                if (!isPaid) {
                    txtFee.setText("0.0");
                }
            }
        });
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(comboEventType, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Reg Fee (Rs.):"), gbc);
        txtFee = new JTextField("0.0");
        txtFee.setEnabled(false); // Default disabled for free events
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtFee, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        txtDate = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtDate, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Venue:"), gbc);
        txtVenue = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtVenue, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Organizer Name:"), gbc);
        txtOrganizer = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtOrganizer, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formFields.add(new JLabel("Description:"), gbc);
        
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(txtDescription);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        formFields.add(descScrollPane, gbc);

        formContainer.add(formFields, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        buttonPanel.setBorder(new EmptyBorder(8, 4, 8, 4));

        JButton btnAdd = new JButton("Create Event");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateEvent();
            }
        });
        buttonPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Update Event");
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateEvent();
            }
        });
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete Event");
        btnDelete.setForeground(new Color(180, 0, 0));
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteEvent();
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

        JButton btnDetails = new JButton("View Full Details");
        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewDetails();
            }
        });
        buttonPanel.add(btnDetails);

        JButton btnAutoId = new JButton("Generate ID");
        btnAutoId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtId.setText("EVT-" + (int)(Math.random() * 900 + 100));
            }
        });
        buttonPanel.add(btnAutoId);

        formContainer.add(buttonPanel, BorderLayout.SOUTH);

       
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, formContainer);
        splitPane.setResizeWeight(0.65);
        add(splitPane, BorderLayout.CENTER);
    }

   
    public void loadEventsToTable() {
        tableModel.setRowCount(0);
        List<Event> events = eventService.getAllEvents();
        
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            String feeDisplay = event.isPaid() ? String.format("Rs. %.2f", event.getRegistrationFee()) : "FREE";
            
            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    feeDisplay,
                    DateTimeUtil.formatDate(event.getDate()),
                    event.getVenue(),
                    event.getOrganizerName(),
                    event.getSessions().size()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow >= 0) {
            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            Event event = eventService.getEventById(eventId);
            
            if (event != null) {
                txtId.setText(event.getEventId());
                txtName.setText(event.getEventName());
                txtDescription.setText(event.getDescription());
                txtDate.setText(DateTimeUtil.formatDate(event.getDate()));
                txtVenue.setText(event.getVenue());
                txtOrganizer.setText(event.getOrganizerName());
                
                if (event.isPaid()) {
                    comboEventType.setSelectedItem("Paid Event");
                    txtFee.setEnabled(true);
                    txtFee.setText(String.valueOf(event.getRegistrationFee()));
                } else {
                    comboEventType.setSelectedItem("Free Event");
                    txtFee.setEnabled(false);
                    txtFee.setText("0.0");
                }
            }
        }
    }

    private void handleCreateEvent() {
        try {
            Event event = extractEventFromForm();
            eventService.createEvent(event);
            
            JOptionPane.showMessageDialog(this,
                    "Event '" + event.getEventName() + "' created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            loadEventsToTable();
            clearForm();
            notifyDataChanged();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateEvent() {
        try {
            Event event = extractEventFromForm();
            boolean updated = eventService.updateEvent(event);
            
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Event '" + event.getEventName() + "' updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEventsToTable();
                notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Event not found for update.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table to delete.",
                    "No Event Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String eventId = (String) tableModel.getValueAt(selectedRow, 0);
        String eventName = (String) tableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete event '" + eventName + "' (" + eventId + ")?\n" +
                "NOTE: This will also remove all associated sessions and registrations.",
                "Confirm Event Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = eventService.deleteEvent(eventId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Event deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadEventsToTable();
                    clearForm();
                    notifyDataChanged();
                } else {
                    JOptionPane.showMessageDialog(this, "Event could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewDetails() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String eventId = (String) tableModel.getValueAt(selectedRow, 0);
        Event event = eventService.getEventById(eventId);
        
        if (event != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("====================================================\n");
            sb.append("                   EVENT DETAILS\n");
            sb.append("====================================================\n\n");
            sb.append("Event ID       : ").append(event.getEventId()).append("\n");
            sb.append("Event Name     : ").append(event.getEventName()).append("\n");
            sb.append("Registration   : ").append(event.isPaid() ? String.format("PAID (Rs. %.2f)", event.getRegistrationFee()) : "FREE").append("\n");
            sb.append("Date           : ").append(DateTimeUtil.formatDate(event.getDate())).append("\n");
            sb.append("Venue          : ").append(event.getVenue()).append("\n");
            sb.append("Organizer      : ").append(event.getOrganizerName()).append("\n\n");
            sb.append("Description    : \n").append(event.getDescription()).append("\n\n");
            sb.append("Attached Sessions: ").append(event.getSessions().size()).append("\n");
            
            for (int i = 0; i < event.getSessions().size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(event.getSessions().get(i).getSummary()).append("\n");
            }
            sb.append("====================================================\n");

            JTextArea textArea = new JTextArea(sb.toString(), 17, 45);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Event Details: " + event.getEventName(), JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        List<Event> results = eventService.searchEvents(keyword);
        
        tableModel.setRowCount(0);
        for (int i = 0; i < results.size(); i++) {
            Event event = results.get(i);
            String feeDisplay = event.isPaid() ? String.format("Rs. %.2f", event.getRegistrationFee()) : "FREE";
            
            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    feeDisplay,
                    DateTimeUtil.formatDate(event.getDate()),
                    event.getVenue(),
                    event.getOrganizerName(),
                    event.getSessions().size()
            });
        }
    }

    private Event extractEventFromForm() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String desc = txtDescription.getText().trim();
        String dateStr = txtDate.getText().trim();
        String venue = txtVenue.getText().trim();
        String organizer = txtOrganizer.getText().trim();

        if (dateStr.isEmpty()) {
            throw new ValidationException("Event Date cannot be empty (format: YYYY-MM-DD).");
        }

        if (!DateTimeUtil.isValidDate(dateStr)) {
            throw new ValidationException("Invalid Date format: '" + dateStr + "'. Please use YYYY-MM-DD (e.g. 2026-06-01).");
        }

        double fee = 0.0;
        if ("Paid Event".equals(comboEventType.getSelectedItem())) {
            String feeStr = txtFee.getText().trim();
            if (feeStr.isEmpty()) {
                throw new ValidationException("Please enter a Registration Fee for the Paid Event.");
            }
            try {
                fee = Double.parseDouble(feeStr);
                if (fee < 0.0) {
                    throw new ValidationException("Registration Fee cannot be negative.");
                }
            } catch (NumberFormatException nfe) {
                throw new ValidationException("Registration Fee must be a valid number (e.g. 150.00).");
            }
        }

        return new Event(id, name, desc, dateStr, venue, organizer, fee);
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtDescription.setText("");
        txtDate.setText("");
        txtVenue.setText("");
        txtOrganizer.setText("");
        comboEventType.setSelectedIndex(0);
        txtFee.setText("0.0");
        txtFee.setEnabled(false);
        eventTable.clearSelection();
    }

    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.onDataChanged();
        }
    }
}