package com.eventmgmt.dao.inmemory;
import com.eventmgmt.dao.SessionDAO;
import com.eventmgmt.model.Session;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class InMemorySessionDAO implements SessionDAO {
    private final Vector<Session> sessionList = new Vector<Session>();
    private static final String STORE_FILE = "sessions_data.txt";

    public InMemorySessionDAO() {
        loadFromFile();
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
                if (parts.length >= 7) {
                    // Using standard String representation for date and time fields to align with syllabus
                    Session session = new Session(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5],
                            parts[6]
                    );
                    sessionList.add(session);
                }
            }
        } catch (Exception e) {
            System.err.println("[Local Storage] Error loading sessions: " + e.getMessage());
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
            writer.println("# Session ID | Event ID | Session Title | Date | Start Time | End Time | Venue");
            
            for (int i = 0; i < sessionList.size(); i++) {
                Session session = sessionList.get(i);
                writer.println(session.getSessionId() + "|" +
                               session.getEventId() + "|" +
                               session.getTitle() + "|" +
                               session.getDate() + "|" +
                               session.getStartTime() + "|" +
                               session.getEndTime() + "|" +
                               session.getVenue());
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error saving sessions: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public synchronized void save(Session session) {
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i).getSessionId().equalsIgnoreCase(session.getSessionId())) {
                sessionList.set(i, session);
                persistToFile();
                return;
            }
        }
        sessionList.add(session);
        persistToFile();
    }

    @Override
    public synchronized Session findById(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            if (session.getSessionId().equalsIgnoreCase(sessionId.trim())) {
                return session;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Session> findByEventId(String eventId) {
        List<Session> matches = new ArrayList<Session>();
        if (eventId == null) {
            return matches;
        }

        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            if (session.getEventId() != null && session.getEventId().equalsIgnoreCase(eventId.trim())) {
                matches.add(session);
            }
        }
        
        
        Collections.sort(matches);
        return matches;
    }

    @Override
    public synchronized boolean update(Session session) {
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i).getSessionId().equalsIgnoreCase(session.getSessionId())) {
                sessionList.set(i, session);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean delete(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i).getSessionId().equalsIgnoreCase(sessionId.trim())) {
                sessionList.remove(i);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteByEventId(String eventId) {
        if (eventId == null) {
            return false;
        }
        
        boolean removedAny = false;
        for (int i = sessionList.size() - 1; i >= 0; i--) {
            Session session = sessionList.get(i);
            if (session.getEventId() != null && session.getEventId().equalsIgnoreCase(eventId.trim())) {
                sessionList.remove(i);
                removedAny = true;
            }
        }
        
        if (removedAny) {
            persistToFile();
        }
        return removedAny;
    }
}