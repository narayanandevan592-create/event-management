package com.eventmgmt.service;

import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Participant;
import java.util.ArrayList;
import java.util.List;

public class ParticipantService{
    private final ParticipantDAO participantDAO;

    public ParticipantService(ParticipantDAO participantDAO){
        this.participantDAO = participantDAO;
    }

    public Participant addParticipant(Participant participant){
        validateParticipant(participant);

        // Ensure unique email check
        Participant existing = participantDAO.findByEmail(participant.getEmail());
        if (existing != null && !existing.getId().equalsIgnoreCase(participant.getId())){
            throw new ValidationException("A participant with email '" + participant.getEmail() + "' already exists.");
        }

        participantDAO.save(participant);
        return participant;
    }

    public List<Participant> getAllParticipants(){
        return participantDAO.findAll();
    }

    public Participant getParticipantById(String id){
        if (id == null || id.trim().isEmpty()){
            return null;
        }
        return participantDAO.findById(id.trim());
    }

    public Participant authenticate(String emailOrId, String password){
        if (emailOrId == null || emailOrId.trim().isEmpty()){
            throw new ValidationException("Please enter your Student Email or ID.");
        }
        if (password == null || password.trim().isEmpty()){
            throw new ValidationException("Please enter your Password.");
        }
        Participant participant = participantDAO.authenticate(emailOrId.trim(), password.trim());
        if (participant == null) {
            throw new ValidationException("Invalid student credentials. Please check your Email/ID and Password.");
        }
        return participant;
    }

    public Participant registerStudent(String studentId, String name, String email, String phone, String password){
        if (studentId == null || studentId.trim().isEmpty()){
            throw new ValidationException("Student ID is required (e.g. STU-101).");
        }
        if (password == null || password.trim().length() < 4){
            throw new ValidationException("Password must be at least 4 characters long.");
        }
        Participant participant = new Participant(studentId.trim(), name, email, phone, password.trim());
        return addParticipant(participant);
    }

    public boolean updateParticipant(Participant participant){
        validateParticipant(participant);

        // Ensure email uniqueness across participants during update
        Participant existing = participantDAO.findByEmail(participant.getEmail());
        if (existing != null && !existing.getId().equalsIgnoreCase(participant.getId())){
            throw new ValidationException("Another participant is already using the email: " + participant.getEmail());
        }

        return participantDAO.update(participant);
    }

    public boolean deleteParticipant(String id){
        if (id == null || id.trim().isEmpty()){
            throw new ValidationException("Participant ID is required for deletion.");
        }
        return participantDAO.delete(id.trim());
    }

    public List<Participant> searchParticipants(String keyword){
        if (keyword == null || keyword.trim().isEmpty()){
            return getAllParticipants();
        }
        String lowerKeyword = keyword.toLowerCase().trim();
        List<Participant> allParticipants = getAllParticipants();
        List<Participant> matchingParticipants = new ArrayList<Participant>();

        for (int i = 0; i < allParticipants.size(); i++){
            Participant participant = allParticipants.get(i);
            if (participant.getName().toLowerCase().contains(lowerKeyword) ||
                participant.getEmail().toLowerCase().contains(lowerKeyword) ||
                participant.getId().toLowerCase().contains(lowerKeyword)){
                matchingParticipants.add(participant);
            }
        }
        return matchingParticipants;
    }

    private void validateParticipant(Participant participant){
        if (participant == null) {
            throw new ValidationException("Participant cannot be null.");
        }
        if (participant.getId() == null || participant.getId().trim().isEmpty()){
            throw new ValidationException("Participant ID is required.");
        }
        if (participant.getName() == null || participant.getName().trim().isEmpty()){
            throw new ValidationException("Full Name is required.");
        }
        if (participant.getEmail() == null || !participant.getEmail().contains("@") || !participant.getEmail().contains(".")){
            throw new ValidationException("A valid Email address (containing '@' and '.') is required.");
        }
    }
}