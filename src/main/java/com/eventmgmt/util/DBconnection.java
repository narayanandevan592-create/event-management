package com.eventmgmt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
public class DBConnection {
    private static final Properties properties = new Properties();
    private static boolean driverLoaded = false;
    private static boolean schemaInitialized = false;

    
    static {
        loadProperties();
    }

   
    public static void loadProperties() {
        boolean loaded = false;

        
        File localFile = new File("db.properties");
        if (localFile.exists()) {
            try (InputStream input = new FileInputStream(localFile)) {
                properties.load(input);
                loaded = true;
            } catch (Exception e) {
                System.err.println("Note: Could not read local configuration file: " + e.getMessage());
            }
        }

        
        if (!loaded) {
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    properties.load(input);
                    loaded = true;
                }
            } catch (Exception e) {
                System.err.println("Note: Could not read classpath configuration: " + e.getMessage());
            }
        }

        
        if (!loaded) {
            properties.setProperty("db.enabled", "true");
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/event_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            properties.setProperty("db.username", "root");
            properties.setProperty("db.password", "");
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }

    public static boolean isDbEnabled() {
        return Boolean.parseBoolean(properties.getProperty("db.enabled", "true"));
    }

    public static void setDbEnabled(boolean enabled) {
        properties.setProperty("db.enabled", String.valueOf(enabled));
    }

   
    public static void tryStartMySqlServer() {
        try {
            File mysqld = new File("C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqld.exe");
            File dataDir = new File("C:\\ProgramData\\MySQL\\MySQL Server 8.4\\Data");
            
            if (mysqld.exists() && dataDir.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        mysqld.getAbsolutePath(),
                        "--datadir=" + dataDir.getAbsolutePath(),
                        "--console"
                );
                processBuilder.redirectErrorStream(true);
                processBuilder.start();
                Thread.sleep(2000); // Allow MySQL time to initialize port 3306
            }
        } catch (Exception ignored) {
        }
    }

   
    public static synchronized void ensureDatabaseAndTablesExist() {
        if (schemaInitialized) {
            return;
        }

        String hostUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String dbUser = properties.getProperty("db.username", "root");
        String dbPass = properties.getProperty("db.password", "");

        
        try (Connection serverConn = DriverManager.getConnection(hostUrl, dbUser, dbPass);
             Statement statement = serverConn.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS event_management_db;");
        } catch (SQLException e) {
            // Handle common root password variations automatically
            if (e.getErrorCode() == 1045) {
                String fallbackPass = dbPass.isEmpty() ? "root" : "";
                try (Connection serverConn = DriverManager.getConnection(hostUrl, dbUser, fallbackPass);
                     Statement statement = serverConn.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE IF NOT EXISTS event_management_db;");
                    properties.setProperty("db.password", fallbackPass);
                    savePropertiesLocal();
                } catch (Exception ignored) {
                }
            }
        }

        
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

           
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS events (" +
                "  event_id VARCHAR(50) PRIMARY KEY," +
                "  event_name VARCHAR(150) NOT NULL," +
                "  description TEXT," +
                "  event_date DATE NOT NULL," +
                "  venue VARCHAR(150) NOT NULL," +
                "  organizer_name VARCHAR(100) NOT NULL," +
                "  registration_fee DOUBLE DEFAULT 0.0," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
            );

            
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS participants (" +
                "  participant_id VARCHAR(50) PRIMARY KEY," +
                "  full_name VARCHAR(100) NOT NULL," +
                "  email VARCHAR(100) NOT NULL UNIQUE," +
                "  phone VARCHAR(30)," +
                "  password VARCHAR(100) NOT NULL DEFAULT 'student123'," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
            );

            
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS admins (" +
                "  admin_id VARCHAR(50) PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(100) NOT NULL," +
                "  full_name VARCHAR(100) NOT NULL," +
                "  email VARCHAR(100)," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
            );

           
            statement.executeUpdate(
                "INSERT INTO admins (admin_id, username, password, full_name, email) " +
                "SELECT 'ADM-001', 'admin', 'admin123', 'System Administrator', 'admin@eventora.com' " +
                "WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'admin');"
            );

            
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS sessions (" +
                "  session_id VARCHAR(50) PRIMARY KEY," +
                "  event_id VARCHAR(50) NOT NULL," +
                "  session_title VARCHAR(150) NOT NULL," +
                "  session_date DATE NOT NULL," +
                "  start_time TIME NOT NULL," +
                "  end_time TIME NOT NULL," +
                "  venue VARCHAR(150) NOT NULL," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  CONSTRAINT fk_session_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE" +
                ");"
            );

           
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS registrations (" +
                "  registration_id VARCHAR(50) PRIMARY KEY," +
                "  event_id VARCHAR(50) NOT NULL," +
                "  participant_id VARCHAR(50) NOT NULL," +
                "  registration_date DATE NOT NULL," +
                "  status VARCHAR(30) DEFAULT 'Confirmed'," +
                "  fee_amount DOUBLE DEFAULT 0.0," +
                "  payment_mode VARCHAR(50) DEFAULT 'OFFLINE'," +
                "  payment_status VARCHAR(30) DEFAULT 'CONFIRMED'," +
                "  payment_notes VARCHAR(255) DEFAULT ''," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE," +
                "  CONSTRAINT fk_reg_participant FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE," +
                "  CONSTRAINT uk_event_participant UNIQUE KEY (event_id, participant_id)" +
                ");"
            );

            schemaInitialized = true;
            System.out.println("[DBConnection] Database schema and tables successfully verified.");
        } catch (SQLException e) {
            System.err.println("[DBConnection] Schema initialization notice: " + e.getMessage());
        }
    }

    private static void savePropertiesLocal() {
        try (FileOutputStream output = new FileOutputStream("db.properties")) {
            properties.store(output, "Updated by DBConnection auto-detection");
        } catch (Exception ignored) {
        }
    }

   
    public static String testConnectionStatus() {
        if (!isDbEnabled()) {
            return "Database mode is currently DISABLED in configuration.";
        }
        try {
            ensureDriverLoaded();
            try (Connection connection = getConnection()) {
                if (connection != null && !connection.isClosed()) {
                    return "SUCCESS: Connected to MySQL (" + connection.getMetaData().getDatabaseProductName() + ") successfully!";
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 0 || e.getMessage().contains("Communications link failure")) {
                tryStartMySqlServer();
                try (Connection connection = getConnection()) {
                    if (connection != null && !connection.isClosed()) {
                        return "SUCCESS: Started local MySQL Server and connected successfully!";
                    }
                } catch (Exception ignored) {
                }
            }
            return "CONNECTION ERROR (" + e.getErrorCode() + "): " + e.getMessage();
        }
        return "FAILURE: Could not connect to MySQL server on localhost:3306.";
    }

   
    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String pass = properties.getProperty("db.password", "");

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1045) { // Access denied fallback check
                String fallbackPass = pass.isEmpty() ? "root" : "";
                try {
                    Connection connection = DriverManager.getConnection(url, user, fallbackPass);
                    properties.setProperty("db.password", fallbackPass);
                    savePropertiesLocal();
                    return connection;
                } catch (SQLException ignored) {
                }
            }
            throw e;
        }
    }

    private static void ensureDriverLoaded() throws SQLException {
        if (!driverLoaded) {
            try {
                String driverClass = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                Class.forName(driverClass);
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver ('com.mysql.cj.jdbc.Driver') not found in classpath.", e);
            }
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}