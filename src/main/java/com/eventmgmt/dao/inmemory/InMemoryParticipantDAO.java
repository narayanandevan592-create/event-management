package com.eventmgmt.dao.inmemory;
import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.model.Participant;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class InMemoryParticipantDAO implements ParticipantDAO {
    private final Vector<Participant> participantList = new Vector<Participant>();
    private static final String STORE_FILE = "participants_data.txt";

    public InMemoryParticipantDAO() {
        loadFromFile();
    }

    /**
     * Loads participant records from the local text storage file[cite: 39].
     */
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
                if (parts.length >= 4) {
                    String password = parts.length >= 5 ? parts[4] : "student123";
                    Participant participant = new Participant(parts[0], parts[1], parts[2], parts[3], password);
                    participantList.add(participant);
                }
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error loading participants: " + e.getMessage());
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
            writer.println("# Participant ID | Full Name | Email Address | Phone Number | Password");
            
            for (int i = 0; i < participantList.size(); i++) {
                Participant participant = participantList.get(i);
                writer.println(participant.getId() + "|" + 
                               participant.getName() + "|" + 
                               participant.getEmail() + "|" + 
                               participant.getPhone() + "|" + 
                               participant.getPassword());
            }
        } catch (IOException e) {
            System.err.println("[Local Storage] Error saving participants: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public synchronized void save(Participant participant) {
        for (int i = 0; i < participantList.size(); i++) {
            if (participantList.get(i).getId().equalsIgnoreCase(participant.getId())) {
                participantList.set(i, participant);
                persistToFile();
                return;
            }
        }
        participantList.add(participant);
        persistToFile();
    }

    @Override
    public synchronized Participant findById(String participantId) {
        if (participantId == null) {
            return null;
        }
        
        for (int i = 0; i < participantList.size(); i++) {
            Participant participant = participantList.get(i);
            if (participant.getId().equalsIgnoreCase(participantId.trim())) {
                return participant;
            }
        }
        return null;
    }

    @Override
    public synchronized Participant findByEmail(String email) {
        if (email == null) {
            return null;
        }
        
        String cleanEmail = email.trim();
        for (int i = 0; i < participantList.size(); i++) {
            Participant participant = participantList.get(i);
            if (participant.getEmail().equalsIgnoreCase(cleanEmail)) {
                return participant;
            }
        }
        return null;
    }

    @Override
    public synchronized Participant authenticate(String emailOrId, String password) {
        if (emailOrId == null || password == null) {
            return null;
        }
        
        String cleanId = emailOrId.trim().toLowerCase();
        String cleanPassword = password.trim();

        for (int i = 0; i < participantList.size(); i++) {
            Participant participant = participantList.get(i);
            if ((participant.getEmail().toLowerCase().equals(cleanId) || participant.getId().toLowerCase().equals(cleanId)) &&
                participant.getPassword().equals(cleanPassword)) {
                return participant;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Participant> findAll() {
        List<Participant> copyList = new ArrayList<Participant>();
        for (int i = 0; i < participantList.size(); i++) {
            copyList.add(participantList.get(i));
        }
        return copyList;
    }

    @Override
    public synchronized boolean update(Participant participant) {
        for (int i = 0; i < participantList.size(); i++) {
            if (participantList.get(i).getId().equalsIgnoreCase(participant.getId())) {
                participantList.set(i, participant);
                persistToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean delete(String participantId) {
        if (participantId == null) {
            return false;
        }
        
        for (int i = 0; i < participantList.size(); i++) {
            if (participantList.get(i).getId().equalsIgnoreCase(participantId.trim())) {
                participantList.remove(i);
                persistToFile();
                return true;
            }
        }
        return false;
    }
}