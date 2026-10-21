package com.eventmgmt.dao.inmemory;
import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.dao.RegistrationDAO;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class InMemoryRegistrationDAO implements RegistrationDAO {
    private final Vector<Registration> registrationList = new Vector<Registration>();
    private final EventDAO eventDAO;
    private final ParticipantDAO participantDAO;
    private static final String STORE_FILE = "registrations_data.txt";

    public InMemoryRegistrationDAO(EventDAO eventDAO, ParticipantDAO participantDAO) {
        this.eventDAO = eventDAO;
        this.participantDAO = participantDAO;
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
                if (parts.length >= 5) {
                    double feeAmount = 0.0;
                    String paymentMode = "OFFLINE";
                    String paymentStatus = "CONFIRMED";
                    String paymentNotes = "";
                    
                    if (parts.length >= 6) {
                        try {
                            feeAmount = Double.parseDouble(parts[5]);
                        } catch (Exception ignored) {
                        }
                    }
                    if (parts.length >= 7) {
                        paymentMode = parts[6];
                    }
                    if (parts.length >= 8) {
                        paymentStatus = parts[7];
                    }
                    if (parts.length >= 9) {
                        paymentNotes = parts[8];
                    }

                  
                    Registration registration = new Registration(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            feeAmount,
                            paymentMode,
                            paymentStatus,
                            paymentNotes
                    );
                    registrationList.add(registration);
                }
            }
        } catch (Exception e) {
            System.err.println("[Local Storage] Error loading registrations: " + e.getMessage());
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
            writer.println("# Registration ID | Event ID | Participant ID | Date | Status | Fee | Mode | PayStatus | Notes");
            
            for (int i = 0; i < registrationList.size(); i++) {
                Registration registration = registrationList.get(i);
                writer.println(registration.getRegistrationId() + "|" +
                               registration.getEventId() + "|" +
                               registration.getParticipantId() + "|" +
                               registration.getRegistrationDate() + "|" +
                               registration.getStatus() + "|" +
                               registration.getFeeAmount() + "|" +
                               registration.getPaymentMode() + "|" +
                               registration.getPaymentStatus() + "|" +
                               registration.getPaymentNotes());
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error saving registrations: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public synchronized void save(Registration registration) {
        for (int i = 0; i < registrationList.size(); i++) {
            if (registrationList.get(i).getRegistrationId().equalsIgnoreCase(registration.getRegistrationId())) {
                registrationList.set(i, registration);
                persistToFile();
                return;
            }
        }
        registrationList.add(registration);
        persistToFile();
    }

    @Override
    public synchronized Registration findById(String registrationId) {
        if (registrationId == null) {
            return null;
        }
        
        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.getRegistrationId().equalsIgnoreCase(registrationId.trim())) {
                hydrateRegistration(registration);
                return registration;
            }
        }
        return null;
    }

    @Override
    public synchronized Registration findByEventAndParticipant(String eventId, String participantId) {
        if (eventId == null || participantId == null) {
            return null;
        }
        
        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.getEventId().equalsIgnoreCase(eventId.trim()) &&
                registration.getParticipantId().equalsIgnoreCase(participantId.trim())) {
                hydrateRegistration(registration);
                return registration;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Registration> findByEventId(String eventId) {
        List<Registration> matches = new ArrayList<Registration>();
        if (eventId == null) {
            return matches;
        }

        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.getEventId().equalsIgnoreCase(eventId.trim())) {
                hydrateRegistration(registration);
                matches.add(registration);
            }
        }
        return matches;
    }

    @Override
    public synchronized List<Registration> findByParticipantId(String participantId) {
        List<Registration> matches = new ArrayList<Registration>();
        if (participantId == null) {
            return matches;
        }

        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.getParticipantId().equalsIgnoreCase(participantId.trim())) {
                hydrateRegistration(registration);
                matches.add(registration);
            }
        }
        return matches;
    }

    @Override
    public synchronized List<Registration> findAll() {
        List<Registration> copyList = new ArrayList<Registration>();
        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            hydrateRegistration(registration);
            copyList.add(registration);
        }
        return copyList;
    }

    @Override
    public synchronized List<Registration> findPendingPayments() {
        List<Registration> pendingList = new ArrayList<Registration>();
        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.isPaymentPending()) {
                hydrateRegistration(registration);
                pendingList.add(registration);
            }
        }
        return pendingList;
    }

    @Override
    public synchronized boolean update(Registration registration) {
        for (int i = 0; i < registrationList.size(); i++) {
            if (registrationList.get(i).getRegistrationId().equalsIgnoreCase(registration.getRegistrationId())) {
                registrationList.set(i, registration);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean updatePaymentStatus(String registrationId, String paymentStatus, String registrationStatus, String notes) {
        if (registrationId == null) {
            return false;
        }
        
        for (int i = 0; i < registrationList.size(); i++) {
            Registration registration = registrationList.get(i);
            if (registration.getRegistrationId().equalsIgnoreCase(registrationId.trim())) {
                registration.setPaymentStatus(paymentStatus);
                registration.setStatus(registrationStatus);
                
                if (notes != null && !notes.trim().isEmpty()) {
                    registration.setPaymentNotes(notes.trim());
                }
                
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean delete(String registrationId) {
        if (registrationId == null) {
            return false;
        }
        
        for (int i = 0; i < registrationList.size(); i++) {
            if (registrationList.get(i).getRegistrationId().equalsIgnoreCase(registrationId.trim())) {
                registrationList.remove(i);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    private void hydrateRegistration(Registration registration) {
        if (registration == null) {
            return;
        }
        if (eventDAO != null) {
            Event event = eventDAO.findById(registration.getEventId());
            if (event != null) {
                registration.setEventName(event.getEventName());
            }
        }
        if (participantDAO != null) {
            Participant participant = participantDAO.findById(registration.getParticipantId());
            if (participant != null) {
                registration.setParticipantName(participant.getName());
                registration.setParticipantEmail(participant.getEmail());
            }
        }
    }
}