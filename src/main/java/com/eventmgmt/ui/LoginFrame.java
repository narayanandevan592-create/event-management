package com.eventmgmt.ui;

import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Admin;
import com.eventmgmt.model.Participant;
import com.eventmgmt.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginFrame extends JFrame {
    private final AuthService authService;
    private JTabbedPane tabbedPane;

    
    private JTextField txtStudentUser;
    private JPasswordField txtStudentPass;

   
    private JTextField txtAdminUser;
    private JPasswordField txtAdminPass;

    
    private JTextField txtSignId;
    private JTextField txtSignName;
    private JTextField txtSignEmail;
    private JTextField txtSignPhone;
    private JPasswordField txtSignPass;
    private JPasswordField txtSignConfirmPass;

    public LoginFrame() {
        super("Eventora — Login & Sign-Up Portal");
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.authService = new AuthService(daoFactory.getAdminDAO(), daoFactory.getParticipantDAO());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);
        setMinimumSize(new Dimension(550, 520));
        setLocationRelativeTo(null); // Center window on screen

        buildUI();
    }

    
    private void buildUI() {
        setLayout(new BorderLayout());

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate Navy
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel("EVENTORA");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Campus Event Management & Student Registration Portal");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

       
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabbedPane.addTab("Student Login", createStudentLoginPanel());
        tabbedPane.addTab("Student Sign-Up", createStudentSignUpPanel());
        tabbedPane.addTab("Admin Login", createAdminLoginPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(241, 245, 249));
        footerPanel.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel footerText = new JLabel("Status: System Ready • MySQL Persistent Storage Supported");
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footerText.setForeground(new Color(71, 85, 105));
        footerPanel.add(footerText);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createStudentLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeading = new JLabel("Welcome Back, Student!");
        lblHeading.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblHeading.setForeground(new Color(21, 101, 192));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblHeading, gbc);

        JLabel lblSubheading = new JLabel("Log in with your Student Email or ID to browse events and manage registrations.");
        lblSubheading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubheading.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        panel.add(lblSubheading, gbc);

        gbc.gridwidth = 1;

       
        JLabel lblUser = new JLabel("Student Email or ID:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblUser, gbc);

        txtStudentUser = new JTextField(20);
        txtStudentUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtStudentUser, gbc);

        
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblPass, gbc);

        txtStudentPass = new JPasswordField(20);
        txtStudentPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtStudentPass, gbc);

      
        JButton btnLogin = UIUtils.createStyledButton("Login as Student", new Color(21, 101, 192), Color.WHITE, 13);
        btnLogin.setPreferredSize(new Dimension(180, 38));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStudentLogin();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        panel.add(btnLogin, gbc);

        // Switch to Sign-Up Tab Button
        JButton btnGoSignUp = new JButton("Don't have an account? Sign Up Here");
        btnGoSignUp.setForeground(new Color(21, 101, 192));
        btnGoSignUp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnGoSignUp.setBorderPainted(false);
        btnGoSignUp.setContentAreaFilled(false);
        btnGoSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(1); // Switch to Sign-Up tab
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(4, 8, 8, 8);
        panel.add(btnGoSignUp, gbc);

        return panel;
    }

   
    private JPanel createStudentSignUpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeading = new JLabel("Create a Student Account");
        lblHeading.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblHeading.setForeground(new Color(16, 185, 129)); // Emerald Green
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblHeading, gbc);

        JLabel lblSubheading = new JLabel("Fill in your details below to register for college events.");
        lblSubheading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubheading.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        panel.add(lblSubheading, gbc);

        gbc.gridwidth = 1;

        
        JLabel lblId = new JLabel("Student ID (e.g. STU-101):");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblId, gbc);

        txtSignId = new JTextField(18);
        gbc.gridx = 1;
        panel.add(txtSignId, gbc);

       
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblName, gbc);

        txtSignName = new JTextField(18);
        gbc.gridx = 1;
        panel.add(txtSignName, gbc);

        
        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblEmail, gbc);

        txtSignEmail = new JTextField(18);
        gbc.gridx = 1;
        panel.add(txtSignEmail, gbc);

       
        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblPhone, gbc);

        txtSignPhone = new JTextField(18);
        gbc.gridx = 1;
        panel.add(txtSignPhone, gbc);

       
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblPass, gbc);

        txtSignPass = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(txtSignPass, gbc);

       
        JLabel lblConfirmPass = new JLabel("Confirm Password:");
        lblConfirmPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(lblConfirmPass, gbc);

        txtSignConfirmPass = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(txtSignConfirmPass, gbc);

        
        JButton btnRegister = UIUtils.createStyledButton("Create Account & Sign Up", new Color(16, 185, 129), Color.WHITE, 13);
        btnRegister.setPreferredSize(new Dimension(220, 38));
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStudentSignUp();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 4, 8);
        panel.add(btnRegister, gbc);

        // Switch to Student Login Tab Button
        JButton btnGoLogin = new JButton("Already have an account? Go to Login");
        btnGoLogin.setForeground(new Color(21, 101, 192));
        btnGoLogin.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnGoLogin.setBorderPainted(false);
        btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(0); // Switch to Student Login tab
            }
        });

        gbc.gridy = 9;
        gbc.insets = new Insets(2, 8, 4, 8);
        panel.add(btnGoLogin, gbc);

        return panel;
    }

   
    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeading = new JLabel("Administrator Control Center");
        lblHeading.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblHeading.setForeground(new Color(15, 23, 42)); // Slate Navy
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblHeading, gbc);

        JLabel lblSubheading = new JLabel("Pre-created Admin account to create events, manage schedules & registrations.");
        lblSubheading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubheading.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        panel.add(lblSubheading, gbc);

        // Credentials Hint Box
        JPanel hintBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hintBox.setBackground(new Color(241, 245, 249));
        hintBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        
        JLabel lblHint = new JLabel("Default Admin Credentials:  Username: admin   |   Password: admin123");
        lblHint.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblHint.setForeground(new Color(30, 41, 59));
        hintBox.add(lblHint);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 8, 12, 8);
        panel.add(hintBox, gbc);

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        // Admin Username
        JLabel lblUser = new JLabel("Admin Username:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblUser, gbc);

        txtAdminUser = new JTextField("admin", 20);
        txtAdminUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtAdminUser, gbc);

        // Admin Password
        JLabel lblPass = new JLabel("Admin Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblPass, gbc);

        txtAdminPass = new JPasswordField("admin123", 20);
        txtAdminPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtAdminPass, gbc);

        // Admin Login Button
        JButton btnLogin = UIUtils.createStyledButton("Login as Administrator", new Color(15, 23, 42), Color.WHITE, 13);
        btnLogin.setPreferredSize(new Dimension(210, 38));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAdminLogin();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        panel.add(btnLogin, gbc);

        return panel;
    }

   

    private void handleStudentLogin() {
        String emailOrId = txtStudentUser.getText();
        String password = new String(txtStudentPass.getPassword());

        try {
            Participant student = authService.loginStudent(emailOrId, password);
            JOptionPane.showMessageDialog(
                    this,
                    "Login Successful!\nWelcome back, " + student.getName() + "!",
                    "Student Authentication",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose(); // Close login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new StudentDashboardFrame(student).setVisible(true);
                }
            });
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Login Failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStudentSignUp() {
        String id = txtSignId.getText();
        String name = txtSignName.getText();
        String email = txtSignEmail.getText();
        String phone = txtSignPhone.getText();
        String password = new String(txtSignPass.getPassword());
        String confirm = new String(txtSignConfirmPass.getPassword());

        try {
            Participant newStudent = authService.signUpStudent(id, name, email, phone, password, confirm);
            JOptionPane.showMessageDialog(
                    this,
                    "Account Created Successfully!\n" +
                    "Welcome, " + newStudent.getName() + "!\n" +
                    "You are now logged into Eventora.",
                    "Sign-Up Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose(); // Close login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new StudentDashboardFrame(newStudent).setVisible(true);
                }
            });
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Sign-Up Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not create account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminLogin() {
        String username = txtAdminUser.getText();
        String password = new String(txtAdminPass.getPassword());

        try {
            Admin admin = authService.loginAdmin(username, password);
            JOptionPane.showMessageDialog(
                    this,
                    "Administrator Access Granted!\nWelcome, " + admin.getName() + "!",
                    "Admin Authentication",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose(); // Close login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new AdminDashboardFrame(admin).setVisible(true);
                }
            });
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Authentication Failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}