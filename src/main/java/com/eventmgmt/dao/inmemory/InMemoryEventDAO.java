package com.eventmgmt.dao.inmemory;
import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.SessionDAO;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Session;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InMemoryEventDAO implements EventDAO {
    private final Vector<Event> eventList = new Vector<Event>();
    private final SessionDAO sessionDAO;
    private static final String STORE_FILE = "events_data.txt";

    public InMemoryEventDAO(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
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
                if (parts.length >= 6) {
                    double fee = 0.0;
                    if (parts.length >= 7) {
                        try {
                            fee = Double.parseDouble(parts[6]);
                        } catch (Exception ignored) {
                        }
                    }
                    
                    
                    Event event = new Event(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], fee);
                    eventList.add(event);
                }
            }
        } catch (Exception e) {
            System.err.println("[Local Storage] Error loading events: " + e.getMessage());
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
            writer.println("# Event ID | Event Name | Description | Date | Venue | Organizer | Registration Fee");
            
            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);
                writer.println(event.getEventId() + "|" +
                               event.getEventName() + "|" +
                               event.getDescription() + "|" +
                               event.getDate() + "|" +
                               event.getVenue() + "|" +
                               event.getOrganizerName() + "|" +
                               event.getRegistrationFee());
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error saving events: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public synchronized void save(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getEventId().equalsIgnoreCase(event.getEventId())) {
                eventList.set(i, event);
                persistToFile();
                return;
            }
        }
        eventList.add(event);
        persistToFile();
    }

    @Override
    public synchronized Event findById(String eventId) {
        if (eventId == null) {
            return null;
        }
        
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            if (event.getEventId().equalsIgnoreCase(eventId.trim())) {
                if (sessionDAO != null) {
                    List<Session> sessions = sessionDAO.findByEventId(event.getEventId());
                    event.setSessions(sessions);
                }
                return event;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Event> findAll() {
        List<Event> copyList = new ArrayList<Event>();
        
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            if (sessionDAO != null) {
                List<Session> sessions = sessionDAO.findByEventId(event.getEventId());
                event.setSessions(sessions);
            }
            copyList.add(event);
        }
        return copyList;
    }

    @Override
    public synchronized List<Event> findByTitle(String titleKeyword) {
        List<Event> matches = new ArrayList<Event>();
        if (titleKeyword == null) {
            return matches;
        }

        String lowerKeyword = titleKeyword.toLowerCase().trim();
        List<Event> allEvents = findAll();
        
        for (int i = 0; i < allEvents.size(); i++) {
            Event event = allEvents.get(i);
            if (event.getEventName().toLowerCase().contains(lowerKeyword)) {
                matches.add(event);
            }
        }
        return matches;
    }

    @Override
    public synchronized List<Event> findByDate(String date) {
        List<Event> matches = new ArrayList<Event>();
        if (date == null) {
            return matches;
        }

        List<Event> allEvents = findAll();
        for (int i = 0; i < allEvents.size(); i++) {
            Event event = allEvents.get(i);
            if (event.getDate().equals(date)) {
                matches.add(event);
            }
        }
        return matches;
    }

    @Override
    public synchronized boolean update(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getEventId().equalsIgnoreCase(event.getEventId())) {
                eventList.set(i, event);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean delete(String eventId) {
        if (eventId == null) {
            return false;
        }
        
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getEventId().equalsIgnoreCase(eventId.trim())) {
                eventList.remove(i);
                persistToFile();
                
                if (sessionDAO != null) {
                    sessionDAO.deleteByEventId(eventId.trim());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized int getRegistrationCount(String eventId) {
       
        return 0;
    }
}