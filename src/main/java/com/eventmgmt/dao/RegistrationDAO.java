package com.eventmgmt.dao;
import com.eventmgmt.model.Registration;
import java.util.List;
public interface RegistrationDAO {
    void save(Registration registration);
    Registration findById(String registrationId);
    Registration findByEventAndParticipant(String eventId, String participantId);
    List<Registration> findByEventId(String eventId);
    List<Registration> findByParticipantId(String participantId);
    List<Registration> findAll();
    List<Registration> findPendingPayments();
    boolean update(Registration registration);
    boolean updatePaymentStatus(String registrationId, String paymentStatus, String registrationStatus, String notes);
    boolean delete(String registrationId);
}