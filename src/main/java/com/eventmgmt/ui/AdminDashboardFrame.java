package com.eventmgmt.ui;
import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.model.Admin;
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


public class AdminDashboardFrame extends JFrame {
    private final Admin currentAdmin;
    private final DAOFactory daoFactory;

    
    private EventService eventService;
    private ParticipantService participantService;
    private SessionService sessionService;
    private RegistrationService registrationService;

    
    private EventPanel eventPanel;
    private ParticipantRegistrationPanel participantRegistrationPanel;
    private SessionSchedulePanel sessionSchedulePanel;

    
    private JLabel lblStatusStorage;
    private JLabel lblStatusInfo;

   
    public AdminDashboardFrame(Admin admin) {
        super("Eventora — Administrator Dashboard (" + (admin != null ? admin.getUsername() : "admin") + ")");
        this.currentAdmin = admin != null ? admin : new Admin("ADM-001", "admin", "admin123", "System Administrator", "admin@eventora.com");
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

    public AdminDashboardFrame() {
        this(new Admin("ADM-001", "admin", "admin123", "System Administrator", "admin@eventora.com"));
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

        // --- 1. File Menu ---
        JMenu menuFile = new JMenu("File");
        
        JMenuItem itemRefresh = new JMenuItem("Refresh All Data");
        itemRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });

        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
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
        menuFile.add(itemLogout);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        menuBar.add(menuFile);

        
        JMenu menuDb = new JMenu("Database");
        
        JMenuItem itemTestConn = new JMenuItem("Test MySQL Connection");
        itemTestConn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = DBConnection.testConnectionStatus();
                JOptionPane.showMessageDialog(
                        AdminDashboardFrame.this, 
                        status, 
                        "Database Connection Status",
                        status.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
                );
                updateStatusBar();
            }
        });

        JMenuItem itemStorageInfo = new JMenuItem("View Active Storage Mode");
        itemStorageInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = daoFactory.getStorageModeDescription();
                JOptionPane.showMessageDialog(
                        AdminDashboardFrame.this,
                        "Current Data Storage Engine:\n" + description + "\n\n" +
                        "Note: If MySQL Server is running, all records are stored persistently.\n" +
                        "If MySQL is unreachable, the system automatically uses In-Memory storage.",
                        "Storage Mode", 
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        menuDb.add(itemTestConn);
        menuDb.add(itemStorageInfo);
        menuBar.add(menuDb);

        JMenu menuHelp = new JMenu("Help");
        
        JMenuItem itemAbout = new JMenuItem("About Eventora Admin Portal");
        itemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        AdminDashboardFrame.this,
                        "===========================================================\n" +
                        "         EVENTORA — ADMINISTRATOR CONTROL CENTER          \n" +
                        "===========================================================\n\n" +
                        "Role: Administrator\n" +
                        "Logged in as: " + currentAdmin.getName() + " (" + currentAdmin.getUsername() + ")\n\n" +
                        "Capabilities:\n" +
                        "• Create, edit, and delete events with validation\n" +
                        "• Schedule, update, and manage presentation sessions\n" +
                        "• View all registered students and full event registration lists\n" +
                        "• Verify offline payments collected at venue registration desks",
                        "About Eventora Admin Portal", 
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        menuHelp.add(itemAbout);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }
    private void buildUI() {
        setLayout(new BorderLayout());

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Modern dark navy / slate
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        
        JPanel leftHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        leftHeader.setOpaque(false);

        JLabel titleLabel = new JLabel("EVENTORA — ADMINISTRATOR DASHBOARD");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        leftHeader.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Campus Event Management — Administrator Control Center");
        subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subTitleLabel.setForeground(new Color(148, 163, 184));
        leftHeader.add(subTitleLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        rightHeader.setOpaque(false);

        JLabel adminBadge = new JLabel("Logged in: " + currentAdmin.getName() + " (" + currentAdmin.getUsername() + ")");
        adminBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        adminBadge.setForeground(new Color(226, 232, 240));
        rightHeader.add(adminBadge);

        JButton btnLogout = UIUtils.createStyledButton("Logout", new Color(220, 38, 38), Color.WHITE, 12);
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        rightHeader.add(btnLogout);

        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        eventPanel = new EventPanel(eventService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onEventDataChanged();
            }
        });

        sessionSchedulePanel = new SessionSchedulePanel(sessionService, eventService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onSessionDataChanged();
            }
        });

        participantRegistrationPanel = new ParticipantRegistrationPanel(
                participantService, eventService, registrationService, new DataChangeListener() {
            @Override
            public void onDataChanged() {
                onParticipantOrRegDataChanged();
            }
        });
        tabbedPane.addTab("Events Management", eventPanel);
        tabbedPane.addTab("Sessions & Schedules", sessionSchedulePanel);
        tabbedPane.addTab("Registrations & Students", participantRegistrationPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(getWidth(), 26));

        lblStatusStorage = new JLabel();
        lblStatusStorage.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusStorage.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStatusStorage, BorderLayout.WEST);

        lblStatusInfo = new JLabel("Role: Administrator | Full Access Mode");
        lblStatusInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusInfo.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStatusInfo, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of the Administrator Dashboard?",
                "Confirm Admin Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginFrame().setVisible(true);
                }
            });
        }
    }

    private void onEventDataChanged() {
        if (sessionSchedulePanel != null) {
            sessionSchedulePanel.refreshEventsDropdown();
        }
        if (participantRegistrationPanel != null) {
            participantRegistrationPanel.refreshAllData();
        }
        updateStatusBar();
    }

    private void onParticipantOrRegDataChanged() {
        if (participantRegistrationPanel != null) {
            participantRegistrationPanel.refreshAllData();
        }
        updateStatusBar();
    }

    private void onSessionDataChanged() {
        if (eventPanel != null) {
            eventPanel.loadEventsToTable();
        }
        updateStatusBar();
    }

    public void refreshAllPanels() {
        if (eventPanel != null) {
            eventPanel.loadEventsToTable();
        }
        if (participantRegistrationPanel != null) {
            participantRegistrationPanel.refreshAllData();
        }
        if (sessionSchedulePanel != null) {
            sessionSchedulePanel.refreshEventsDropdown();
        }
        updateStatusBar();
    }

    private void updateStatusBar() {
        String modeDescription = daoFactory.getStorageModeDescription();
        lblStatusStorage.setText("Active Storage Engine: " + modeDescription);
    }
}