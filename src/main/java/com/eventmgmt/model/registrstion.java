package com.eventmgmt.model;
import com.eventmgmt.model.interfaces.Displayable;
public class Registration implements Displayable {
    private String registrationId;
    private String eventId;
    private String participantId;
    private String registrationDate; 
    private String status; 

    
    private double feeAmount = 0.0;
    private String paymentMode = "OFFLINE"; 
    private String paymentStatus = "CONFIRMED"; 
    private String paymentNotes = "";

    
    private String eventName;
    private String participantName;
    private String participantEmail;

    
    private static String generateDefaultId() {
        int randomNum = (int) (Math.random() * 90000) + 10000;
        return "REG-" + randomNum;
    }

   
    public Registration() {
        this.registrationId = generateDefaultId();
        this.registrationDate = "2026-06-01"; 
        this.status = "Confirmed";
        this.feeAmount = 0.0;
        this.paymentMode = "FREE";
        this.paymentStatus = "CONFIRMED";
        this.paymentNotes = "";
    }

    
    public Registration(String eventId, String participantId) {
        this(generateDefaultId(), eventId, participantId, "2026-06-01", "Confirmed");
    }

    
    public Registration(String registrationId, String eventId, String participantId,
                        String registrationDate, String status) {
        this(registrationId, eventId, participantId, registrationDate, status, 0.0, "FREE", "CONFIRMED", "");
    }

    
    public Registration(String registrationId, String eventId, String participantId,
                        String registrationDate, String status,
                        double feeAmount, String paymentMode, String paymentStatus, String paymentNotes) {
        this.registrationId = (registrationId != null && !registrationId.trim().isEmpty()) 
                ? registrationId : generateDefaultId();
        this.eventId = eventId;
        this.participantId = participantId;
        this.registrationDate = (registrationDate != null && !registrationDate.trim().isEmpty()) 
                ? registrationDate : "2026-06-01";
        this.status = status != null ? status : "Confirmed";
        this.feeAmount = Math.max(0.0, feeAmount);
        this.paymentMode = paymentMode != null ? paymentMode : "OFFLINE";
        this.paymentStatus = paymentStatus != null ? paymentStatus : "CONFIRMED";
        this.paymentNotes = paymentNotes != null ? paymentNotes : "";
    }

    
    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = Math.max(0.0, feeAmount);
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = (paymentMode != null) ? paymentMode : "OFFLINE";
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = (paymentStatus != null) ? paymentStatus : "CONFIRMED";
    }

    public String getPaymentNotes() {
        return paymentNotes;
    }

    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = (paymentNotes != null) ? paymentNotes : "";
    }

    public boolean isPaymentPending() {
        return "PENDING_APPROVAL".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(paymentStatus);
    }

    
    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Registration ID: " + registrationId);
        System.out.println("Event ID       : " + eventId + (eventName != null ? " (" + eventName + ")" : ""));
        System.out.println("Participant ID : " + participantId + (participantName != null ? " (" + participantName + ")" : ""));
        System.out.println("Date           : " + registrationDate);
        System.out.println("Reg Fee        : " + (feeAmount > 0.0 ? String.format("Rs. %.2f", feeAmount) : "FREE"));
        System.out.println("Payment Mode   : " + paymentMode);
        System.out.println("Payment Status : " + paymentStatus);
        System.out.println("Notes          : " + paymentNotes);
        System.out.println("Status         : " + status);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getSummary() {
        return String.format("Reg [%s]: %s registered for %s on %s (%s)",
                registrationId,
                participantName != null ? participantName : participantId,
                eventName != null ? eventName : eventId,
                registrationDate,
                status);
    }

    @Override
    public String toString() {
        return "Registration #" + registrationId + " [" + status + "]";
    }
}