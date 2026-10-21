package com.eventmgmt.model;

import com.eventmgmt.model.interfaces.Displayable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Displayable {
    private String eventId;
    private String eventName;
    private String description;
    private String date;
    private String venue;
    private String organizerName;
    private double registrationFee;
    private List<Session> sessions;

    public Event() {
        this("", "", "", "", "", "", 0.0);
    }

    public Event(String eventId, String eventName, String description, String date, String venue, String organizerName) {
        this(eventId, eventName, description, date, venue, organizerName, 0.0);
    }

    public Event(String eventId, String eventName, String description, String date, String venue, String organizerName, double registrationFee) {
        this.eventId = (eventId != null) ? eventId.trim() : "";
        this.eventName = (eventName != null) ? eventName.trim() : "";
        this.description = (description != null) ? description.trim() : "";
        this.date = (date != null) ? date.trim() : "";
        this.venue = (venue != null) ? venue.trim() : "";
        this.organizerName = (organizerName != null) ? organizerName.trim() : "";
        this.registrationFee = Math.max(0.0, registrationFee);
        this.sessions = new ArrayList<Session>();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = (eventId != null) ? eventId.trim() : "";
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = (eventName != null) ? eventName.trim() : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = (date != null) ? date.trim() : "";
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = (venue != null) ? venue.trim() : "";
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = (organizerName != null) ? organizerName.trim() : "";
    }

    public double getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(double registrationFee) {
        this.registrationFee = Math.max(0.0, registrationFee);
    }

    public boolean isPaid() {
        return registrationFee > 0.0;
    }

    public List<Session> getSessions() {
        if (sessions == null) {
            sessions = new ArrayList<Session>();
        }
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = (sessions != null) ? sessions : new ArrayList<Session>();
    }

    public void addSession(Session session) {
        if (sessions == null) {
            sessions = new ArrayList<Session>();
        }
        if (session != null) {
            sessions.add(session);
        }
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Event ID       : " + eventId);
        System.out.println("Event Name     : " + eventName);
        System.out.println("Description    : " + description);
        System.out.println("Date           : " + date);
        System.out.println("Venue          : " + venue);
        System.out.println("Organizer      : " + organizerName);
        System.out.println("Fee            : " + (isPaid() ? String.format("Rs. %.2f", registrationFee) : "FREE"));
        System.out.println("Sessions Count : " + (sessions != null ? sessions.size() : 0));
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getSummary() {
        return String.format("[%s] %s (%s at %s)", eventId, eventName, date, venue);
    }

    @Override
    public String toString() {
        return eventName + " (" + eventId + ")";
    }
}
