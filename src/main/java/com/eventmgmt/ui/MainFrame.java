package com.eventmgmt.ui;

import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.service.EventService;
import com.eventmgmt.service.ParticipantService;
import com.eventmgmt.service.RegistrationService;
import com.eventmgmt.service.SessionService;
import com.eventmgmt.util.DBConnection;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private final DAOFactory daoFactory;
    private EventService eventService;
    private ParticipantService participantService;
    private SessionService sessionService;
    private RegistrationService registrationService;

    // UI Panels for Tabbed Navigation
    private EventPanel eventPanel;
    private ParticipantRegistrationPanel participantRegistrationPanel;
    private SessionSchedulePanel sessionSchedulePanel;

    // Status bar labels for dynamic updates
    private JLabel lblStatusStorage;
    private JLabel lblStatusInfo;

    public MainFrame() {
        super("Java Event Management System — Academic OOP Project");
        this.daoFactory = DAOFactory.getInstance();
        initServices();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null); // Center window on screen

        buildMenuBar();
        buildUI();
        updateStatusBar();
    }


    private void initServices() {
        this.eventService = new EventService(daoFactory.getEventDAO(), daoFactory.getSessionDAO());
        this.participantService = new ParticipantService(daoFactory.getParticipantDAO());
        this.sessionService = new SessionService(daoFactory.getSessionDAO(), daoFactory.getEventDAO());
        this.registrationService = new RegistrationService(
                daoFactory.getRegistrationDAO(), daoFactory.getEventDAO(), daoFactory.getParticipantDAO());
    }

    
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- File Menu ---
        JMenu menuFile = new JMenu("File");
        
        JMenuItem itemRefresh = new JMenuItem("Refresh All Data");
        itemRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });

        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuFile.add(itemRefresh);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        menuBar.add(menuFile);

        // --- Database Menu ---
        JMenu menuDb = new JMenu("Database");
        
        JMenuItem itemTestConn = new JMenuItem("Test MySQL Connection");
        itemTestConn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String statusMessage = DBConnection.testConnectionStatus();
                JOptionPane.showMessageDialog(
                        MainFrame.this, 
                        statusMessage, 
                        "Database Connection Status",
                        statusMessage.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
                );
            }
        });
        menuDb.add(itemTestConn);
        menuBar.add(menuDb);

        // --- Help Menu ---
        JMenu menuHelp = new JMenu("Help");
        
        JMenuItem itemAbout = new JMenuItem("About This Project");
        itemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        menuHelp.add(itemAbout);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }


    private void buildUI() {
        setLayout(new BorderLayout());

        // Header Banner Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("JAVA EVENT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subTitleLabel = new JLabel("BTech Computer Science Academic OOP Project");
        subTitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        subTitleLabel.setForeground(new Color(200, 220, 255));
        headerPanel.add(subTitleLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Central Tabbed Navigation Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Instantiate panels using anonymous inner classes for data change notifications
        eventPanel = new EventPanel(eventService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onEventDataChanged();
            }
        });

        participantRegistrationPanel = new ParticipantRegistrationPanel(
                participantService, eventService, registrationService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onParticipantOrRegDataChanged();
            }
        });

        sessionSchedulePanel = new SessionSchedulePanel(sessionService, eventService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onSessionDataChanged();
            }
        });

        tabbedPane.addTab("Events Management", eventPanel);
        tabbedPane.addTab("Participants & Registrations", participantRegistrationPanel);
        tabbedPane.addTab("Sessions & Schedules", sessionSchedulePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(getWidth(), 24));

        lblStatusStorage = new JLabel();
        lblStatusStorage.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusStorage.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStatusStorage, BorderLayout.WEST);

        lblStatusInfo = new JLabel("Ready | Event-Driven Architecture with Swing & JDBC");
        lblStatusInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusInfo.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStatusInfo, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void updateStatusBar() {
        lblStatusStorage.setText("Storage Mode: " + daoFactory.getStorageModeDescription());
    }

    // --- Inter-Panel Event Synchronization Handlers ---

    private void onEventDataChanged() {
        sessionSchedulePanel.refreshEventsDropdown();
        participantRegistrationPanel.refreshAllData();
        updateStatusBar();
    }

    private void onParticipantOrRegDataChanged() {
        participantRegistrationPanel.refreshAllData();
        updateStatusBar();
    }

    private void onSessionDataChanged() {
        eventPanel.loadEventsToTable();
        updateStatusBar();
    }

    public void refreshAllPanels() {
        eventPanel.loadEventsToTable();
        participantRegistrationPanel.refreshAllData();
        sessionSchedulePanel.refreshEventsDropdown();
        updateStatusBar();
    }

    private void showAboutDialog() {
        String aboutMessage = 
            "Eventora - Campus Event Management Application\n\n" +
            "Built with:\n" +
            "- Java Standard Edition (Swing GUI)\n" +
            "- MySQL & JDBC (Relational Database Storage)\n\n" +
            "Features & OOP Architecture:\n" +
            "1. Encapsulation (Private fields with validated getters/setters)\n" +
            "2. Inheritance (Abstract User superclass with Participant & Organizer subclasses)\n" +
            "3. Polymorphism (Dynamic method dispatch & Interface references)\n" +
            "4. Abstraction (Abstract classes & Interfaces: Displayable, Schedulable, DAOs)\n" +
            "5. Composition (Event HAS-A collection of Session objects)\n" +
            "6. Association (Participant registers for Event via Registration entity)\n" +
            "7. Delegation Event Model (Anonymous Inner Classes for ActionListeners)\n" +
            "8. JDBC (Connection, PreparedStatement, ResultSet, CRUD operations)\n" +
            "9. Custom Exceptions (ValidationException, InvalidSessionTimeException, etc.)\n" +
            "10. Collections (Vector, ArrayList, natural sorting with Comparable)\n";
        
        JOptionPane.showMessageDialog(this, aboutMessage, "About Event Management System", JOptionPane.INFORMATION_MESSAGE);
    }
}