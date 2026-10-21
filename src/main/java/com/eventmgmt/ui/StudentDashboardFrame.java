package com.eventmgmt.ui;

import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.model.Participant;
import com.eventmgmt.service.EventService;
import com.eventmgmt.service.RegistrationService;
import com.eventmgmt.service.SessionService;
import com.eventmgmt.util.DBConnection;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class StudentDashboardFrame extends JFrame {
    private final Participant currentStudent;
    private final DAOFactory daoFactory;
    private EventService eventService;
    private SessionService sessionService;
    private RegistrationService registrationService;

   
    private StudentBrowseEventsPanel browsePanel;
    private StudentMyRegistrationsPanel myRegistrationsPanel;

    private JLabel lblStatusStorage;

    public StudentDashboardFrame(Participant student) {
        super("Eventora — Student Portal (" + student.getName() + ")");
        this.currentStudent = student;
        this.daoFactory = DAOFactory.getInstance();
        initServices();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // Center window on screen

        buildMenuBar();
        buildUI();
    }

   
    private void initServices() {
        this.eventService = new EventService(daoFactory.getEventDAO(), daoFactory.getSessionDAO());
        this.sessionService = new SessionService(daoFactory.getSessionDAO(), daoFactory.getEventDAO());
        this.registrationService = new RegistrationService(
                daoFactory.getRegistrationDAO(), daoFactory.getEventDAO(), daoFactory.getParticipantDAO());
    }

    
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- Account Menu ---
        JMenu menuAccount = new JMenu("Account");
        
        JMenuItem itemProfile = new JMenuItem("My Profile Details");
        itemProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProfileDialog();
            }
        });

        JMenuItem itemRefresh = new JMenuItem("Refresh All Data");
        itemRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAll();
            }
        });

        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        menuAccount.add(itemProfile);
        menuAccount.add(itemRefresh);
        menuAccount.addSeparator();
        menuAccount.add(itemLogout);
        menuBar.add(menuAccount);

      
        JMenu menuDb = new JMenu("Database");
        
        JMenuItem itemTest = new JMenuItem("Test Connection Status");
        itemTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = DBConnection.testConnectionStatus();
                JOptionPane.showMessageDialog(StudentDashboardFrame.this, status, "Database Status", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuDb.add(itemTest);
        menuBar.add(menuDb);

      
        JMenu menuHelp = new JMenu("Help");
        
        JMenuItem itemAbout = new JMenuItem("About Eventora");
        itemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        StudentDashboardFrame.this,
                        "Eventora — Event Management & Registration System\n" +
                        "Student Portal Module\n" +
                        "Campus Event Management Application",
                        "About Eventora", 
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

        // Header Banner with professional blue styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(21, 101, 192)); // Student Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("EVENTORA — STUDENT PORTAL");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeader.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, " + currentStudent.getName() + " (" + currentStudent.getEmail() + ")");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLabel.setForeground(new Color(230, 240, 255));
        rightHeader.add(userLabel);

        JButton btnLogout = UIUtils.createStyledButton("Logout", new Color(183, 28, 28), Color.WHITE, 12);
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

        DataChangeListener listener = new DataChangeListener() {
            @Override
            public void onDataChanged() {
                refreshAll();
            }
        };

        browsePanel = new StudentBrowseEventsPanel(eventService, sessionService, registrationService, currentStudent, listener);
        myRegistrationsPanel = new StudentMyRegistrationsPanel(registrationService, eventService, sessionService, currentStudent, listener);

        tabbedPane.addTab("Browse Available Events & Register", browsePanel);
        tabbedPane.addTab("My Registered Events", myRegistrationsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(getWidth(), 24));

        lblStatusStorage = new JLabel("Storage: " + daoFactory.getStorageModeDescription());
        lblStatusStorage.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusStorage.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStatusStorage, BorderLayout.WEST);

        JLabel lblStudentInfo = new JLabel("Logged in as: " + currentStudent.getName() + " (Student ID: " + currentStudent.getId() + ")");
        lblStudentInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStudentInfo.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.add(lblStudentInfo, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void refreshAll() {
        browsePanel.loadEvents();
        myRegistrationsPanel.loadMyRegistrations();
        lblStatusStorage.setText("Storage: " + daoFactory.getStorageModeDescription());
    }

    private void showProfileDialog() {
        String profileInfo = 
            "==============================================\n" +
            "            STUDENT PROFILE DETAILS           \n" +
            "==============================================\n\n" +
            "Student ID    : " + currentStudent.getId() + "\n" +
            "Full Name     : " + currentStudent.getName() + "\n" +
            "Email Address : " + currentStudent.getEmail() + "\n" +
            "Phone Number  : " + (currentStudent.getPhone().isEmpty() ? "N/A" : currentStudent.getPhone()) + "\n" +
            "Role          : " + currentStudent.getRoleDescription() + "\n" +
            "==============================================\n";

        JTextArea area = new JTextArea(profileInfo, 12, 40);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of your student account?",
                "Confirm Logout",
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
}
