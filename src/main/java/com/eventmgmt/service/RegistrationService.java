package com.eventmgmt.service;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.dao.RegistrationDAO;
import com.eventmgmt.exception.DuplicateRegistrationException;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;

import com.eventmgmt.util.DateTimeUtil;
import java.util.List;

public class RegistrationService{
    private final RegistrationDAO registrationDAO;
    private final EventDAO eventDAO;
    private final ParticipantDAO participantDAO;

    public RegistrationService(RegistrationDAO registrationDAO, EventDAO eventDAO, ParticipantDAO participantDAO){
        this.registrationDAO = registrationDAO;
        this.eventDAO = eventDAO;
        this.participantDAO = participantDAO;
    }

    public Registration registerParticipant(String eventId, String participantId){
        return registerParticipantWithPayment(eventId, participantId, "");
    }

    public Registration registerParticipantWithPayment(String eventId, String participantId, String paymentNotes){
        if (eventId == null || eventId.trim().isEmpty()){
            throw new ValidationException("Please select an Event to register for.");
        }
        if (participantId == null || participantId.trim().isEmpty()){
            throw new ValidationException("Please select a Participant.");
        }
        Event event = eventDAO.findById(eventId.trim());
        if (event == null) {
            throw new ValidationException("Selected event not found in system.");
        }
        Participant participant = participantDAO.findById(participantId.trim());
        if (participant == null) {
            throw new ValidationException("Selected participant not found in system.");
        }
        if (isParticipantRegistered(eventId.trim(), participantId.trim())){
            throw new DuplicateRegistrationException(participant.getName(), event.getEventName());
        }
        Registration reg = new Registration(eventId.trim(), participantId.trim());
        reg.setEventName(event.getEventName());
        reg.setParticipantName(participant.getName());
        reg.setParticipantEmail(participant.getEmail());
        reg.setRegistrationDate(DateTimeUtil.getCurrentDate());
        if (event.isPaid()) {
            reg.setFeeAmount(event.getRegistrationFee());
            reg.setPaymentMode("OFFLINE");
            reg.setPaymentStatus("PENDING_APPROVAL");
            reg.setStatus("Pending Payment"); 
            String formattedFee = String.format("%.2f", event.getRegistrationFee());
            String defaultNote = "Offline payment of Rs. " + formattedFee + " at " + event.getVenue();
            if (paymentNotes != null && !paymentNotes.trim().isEmpty()){
                reg.setPaymentNotes(paymentNotes.trim());
            }
            else{
                reg.setPaymentNotes(defaultNote);
            }
        } 
        else{
            reg.setFeeAmount(0.0);
            reg.setPaymentMode("FREE");
            reg.setPaymentStatus("CONFIRMED");
            reg.setStatus("Confirmed");
            reg.setPaymentNotes("Free Event - Direct Enrollment");
        }
        registrationDAO.save(reg);
        return reg;
    }

    public boolean confirmPayment(String registrationId){
        if (registrationId == null || registrationId.trim().isEmpty()){
            throw new ValidationException("Registration ID is required.");
        }
        Registration reg = registrationDAO.findById(registrationId.trim());
        if (reg == null){
            throw new ValidationException("Registration record not found.");
        }
        return registrationDAO.updatePaymentStatus(registrationId.trim(), "CONFIRMED", "Confirmed",
                "Payment verified and accepted by Administrator.");
    }

    public boolean rejectPayment(String registrationId, String reason){
        if (registrationId == null || registrationId.trim().isEmpty()){
            throw new ValidationException("Registration ID is required.");
        }
        Registration reg = registrationDAO.findById(registrationId.trim());
        if (reg == null) {
            throw new ValidationException("Registration record not found.");
        }
        String note = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "Offline payment was not received at venue desk.";
        return registrationDAO.updatePaymentStatus(registrationId.trim(), "REJECTED", "Payment Rejected", note);
    }

    public List<Registration> getPendingPayments(){
        return registrationDAO.findPendingPayments();
    }

    public boolean isParticipantRegistered(String eventId, String participantId){
        Registration reg = registrationDAO.findByEventAndParticipant(eventId, participantId);
        return reg != null;
    }

    public boolean isParticipantRegistered(Registration registration){
        if (registration == null){
            return false;
        }
        return isParticipantRegistered(registration.getEventId(), registration.getParticipantId());
    }

    public List<Registration> getAllRegistrations(){
        return registrationDAO.findAll();
    }

    public List<Registration> getRegistrationsForEvent(String eventId){
        if (eventId == null || eventId.trim().isEmpty()){
            return getAllRegistrations();
        }
        return registrationDAO.findByEventId(eventId.trim());
    }

    public List<Registration> getRegistrationsForParticipant(String participantId){
        if (participantId == null || participantId.trim().isEmpty()){
            return getAllRegistrations();
        }
        return registrationDAO.findByParticipantId(participantId.trim());
    }

    public boolean cancelRegistration(String registrationId){
        if (registrationId == null || registrationId.trim().isEmpty()){
            throw new ValidationException("Registration ID is required.");
        }
        return registrationDAO.delete(registrationId.trim());
    }
}