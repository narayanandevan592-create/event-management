package com.eventmgmt.dao.inmemory;
import com.eventmgmt.dao.AdminDAO;
import com.eventmgmt.model.Admin;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;


public class InMemoryAdminDAO implements AdminDAO {
    private final Vector<Admin> adminList = new Vector<Admin>();
    private static final String STORE_FILE = "admins_data.txt";

    public InMemoryAdminDAO() {
        loadFromFile();
        if (adminList.isEmpty()) {
            // Seed default pre-created administrator account
            Admin defaultAdmin = new Admin("ADM-001", "admin", "admin123", "System Administrator", "admin@eventora.com");
            adminList.add(defaultAdmin);
            persistToFile();
        }
    }

    
    private synchronized void loadFromFile() {
        File file = new File(STORE_FILE);
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    Admin admin = new Admin(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    adminList.add(admin);
                }
            }
        } catch (Exception e) {
            System.err.println("[Local Storage] Error loading admins: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    
    private synchronized void persistToFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(STORE_FILE), StandardCharsets.UTF_8));
            writer.println("# Admin ID | Username | Password | Full Name | Email");
            
            for (int i = 0; i < adminList.size(); i++) {
                Admin admin = adminList.get(i);
                writer.println(admin.getId() + "|" + admin.getUsername() + "|" + admin.getPassword() + "|" + admin.getName() + "|" + admin.getEmail());
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error saving admins: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public synchronized Admin authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        
        String cleanUsername = username.trim().toLowerCase();
        String cleanPassword = password.trim();

        for (int i = 0; i < adminList.size(); i++) {
            Admin admin = adminList.get(i);
            if (admin.getUsername().toLowerCase().equals(cleanUsername) && admin.getPassword().equals(cleanPassword)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public synchronized Admin findByUsername(String username) {
        if (username == null) {
            return null;
        }
        
        String cleanUsername = username.trim().toLowerCase();

        for (int i = 0; i < adminList.size(); i++) {
            Admin admin = adminList.get(i);
            if (admin.getUsername().toLowerCase().equals(cleanUsername)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public synchronized void save(Admin admin) {
        for (int i = 0; i < adminList.size(); i++) {
            if (adminList.get(i).getUsername().equalsIgnoreCase(admin.getUsername())) {
                adminList.set(i, admin);
                persistToFile();
                return;
            }
        }
        adminList.add(admin);
        persistToFile();
    }
}