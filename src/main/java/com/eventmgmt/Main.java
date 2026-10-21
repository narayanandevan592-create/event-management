package com.eventmgmt;
import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.ui.LoginFrame;
import javax.swing.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
public class Main {

    public static void main(String[] args) {
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                logError("Uncaught Exception in thread " + thread.getName(), throwable);
            }
        });

        printWelcomeBanner();

        try {
            
            DAOFactory factory = DAOFactory.getInstance();
            System.out.println("-> Active Storage Engine: " + factory.getStorageModeDescription());
            System.out.println("-> Launching Desktop User Interface...");
            System.out.println("==================================================================\n");
        } catch (Throwable t) {
            logError("DAOFactory initialization error", t);
        }

      
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Apply the native operating system look and feel for a polished user experience
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Throwable e) {
                    System.err.println("Note: Could not apply system Look and Feel: " + e.getMessage());
                }

                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                } catch (Throwable t) {
                    logError("Error displaying LoginFrame", t);
                    JOptionPane.showMessageDialog(
                            null,
                            "Error initializing Event Management Application:\n" + t.getMessage(),
                            "Application Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private static void printWelcomeBanner() {
        System.out.println("==================================================================");
        System.out.println("                 EVENTORA — EVENT MANAGEMENT SYSTEM               ");
        System.out.println("            Campus Event Management & Registration Portal         ");
        System.out.println("==================================================================");
    }

    private static void logError(String context, Throwable throwable) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("app_error.log", true));
            writer.println("=== " + new Date() + " : " + context + " ===");
            throwable.printStackTrace(writer);
            writer.println();
        } catch (Exception ignored) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}