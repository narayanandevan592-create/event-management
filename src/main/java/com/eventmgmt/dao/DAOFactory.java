package com.eventmgmt.dao;
import com.eventmgmt.dao.inmemory.*;
import com.eventmgmt.dao.jdbc.*;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;

public class DAOFactory {
    private static DAOFactory instance;

    private boolean usingJdbc;
    private EventDAO eventDAO;
    private ParticipantDAO participantDAO;
    private SessionDAO sessionDAO;
    private RegistrationDAO registrationDAO;
    private AdminDAO adminDAO;

    private DAOFactory() {
        initDAOs();
    }

   
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public synchronized void initDAOs() {
        if (DBConnection.isDbEnabled()) {
            if (!isDatabaseReachable()) {
                DBConnection.tryStartMySqlServer();
            }

            if (isDatabaseReachable()) {
                try {
                    DBConnection.ensureDatabaseAndTablesExist();
                    this.eventDAO = new JdbcEventDAO();
                    this.participantDAO = new JdbcParticipantDAO();
                    this.sessionDAO = new JdbcSessionDAO();
                    this.registrationDAO = new JdbcRegistrationDAO();
                    this.adminDAO = new JdbcAdminDAO();
                    this.usingJdbc = true;
                    System.out.println("[DAOFactory] Connected to MySQL Database. Persistent storage ACTIVE.");
                    return;
                } catch (Exception e) {
                    System.err.println("[DAOFactory] Error initializing MySQL DAOs: " + e.getMessage());
                }
            }
        }

        this.sessionDAO = new InMemorySessionDAO();
        this.participantDAO = new InMemoryParticipantDAO();
        this.eventDAO = new InMemoryEventDAO(this.sessionDAO);
        this.registrationDAO = new InMemoryRegistrationDAO(this.eventDAO, this.participantDAO);
        this.adminDAO = new InMemoryAdminDAO();
        this.usingJdbc = false;
        System.out.println("[DAOFactory] Operating in File-Backed Local Engine (Data saved to disk).");
    }

    public boolean isDatabaseReachable() {
        try (Connection connection = DBConnection.getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    
    public synchronized void switchToJdbc() {
        DBConnection.ensureDatabaseAndTablesExist();
        this.eventDAO = new JdbcEventDAO();
        this.participantDAO = new JdbcParticipantDAO();
        this.sessionDAO = new JdbcSessionDAO();
        this.registrationDAO = new JdbcRegistrationDAO();
        this.adminDAO = new JdbcAdminDAO();
        this.usingJdbc = true;
    }

   
    public synchronized void switchToInMemory() {
        this.sessionDAO = new InMemorySessionDAO();
        this.participantDAO = new InMemoryParticipantDAO();
        this.eventDAO = new InMemoryEventDAO(this.sessionDAO);
        this.registrationDAO = new InMemoryRegistrationDAO(this.eventDAO, this.participantDAO);
        this.adminDAO = new InMemoryAdminDAO();
        this.usingJdbc = false;
    }

   

    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public ParticipantDAO getParticipantDAO() {
        return participantDAO;
    }

    public SessionDAO getSessionDAO() {
        return sessionDAO;
    }

    public RegistrationDAO getRegistrationDAO() {
        return registrationDAO;
    }

    public AdminDAO getAdminDAO() {
        return adminDAO;
    }

    public boolean isUsingJdbc() {
        return usingJdbc;
    }

    public String getStorageModeDescription() {
        return usingJdbc ? "MySQL Database (JDBC Connected - Port 3306)" : "Local Persistent Storage (MySQL Offline)";
    }
}