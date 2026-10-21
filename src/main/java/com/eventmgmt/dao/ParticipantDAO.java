package com.eventmgmt.dao;
import com.eventmgmt.model.Participant;
import java.util.List;
public interface ParticipantDAO {
    void save(Participant participant);
    Participant findById(String participantId);
    Participant findByEmail(String email);
    Participant authenticate(String emailOrId, String password);
    List<Participant> findAll();
    boolean update(Participant participant);
    boolean delete(String participantId);
}