package com.eventmgmt.model;

import com.eventmgmt.exception.InvalidSessionTimeException;
import com.eventmgmt.model.interfaces.Displayable;
import com.eventmgmt.model.interfaces.Schedulable;


public class Session implements Schedulable, Displayable, Comparable<Session> {
    private String sessionId;
    private String eventId;
    private String title;
    private String date;      
    private String startTime; 
    private String endTime;   
    private String venue;

    
    public Session() {
    }

    
    public Session(String sessionId, String eventId, String title, String date,
                   String startTime, String endTime, String venue) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        setScheduleTimes(startTime, endTime);
        this.venue = venue;
    }

    
    public void setScheduleTimes(String start, String end) {
        if (start == null || end == null || start.trim().isEmpty() || end.trim().isEmpty()) {
            throw new InvalidSessionTimeException("Session start and end times cannot be empty.");
        }
        
        // Lexicographical comparison works neatly for standard "HH:mm" time strings
        if (end.compareTo(start) <= 0) {
            throw new InvalidSessionTimeException(
                "Invalid Schedule: End time (" + end + ") must be after start time (" + start + ")."
            );
        }
        
        this.startTime = start;
        this.endTime = end;
    }

    
    @Override
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startTotalMins = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endTotalMins = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            
            return Math.max(0, endTotalMins - startTotalMins);
        } catch (Exception e) {
            // Fallback safe return if parsing fails
            return 0;
        }
    }

    
    @Override
    public int compareTo(Session other) {
        if (other == null) {
            return 1;
        }
        int dateComparison = this.date.compareTo(other.date);
        if (dateComparison != 0) {
            return dateComparison;
        }
        return this.startTime.compareTo(other.startTime);
    }


    @Override
    public void displaySchedule() {
        System.out.println(String.format("   -> [%s] %s (%s to %s) at %s",
                sessionId, title, startTime, endTime, venue));
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Session ID   : " + sessionId);
        System.out.println("Title        : " + title);
        System.out.println("Time         : " + startTime + " - " + endTime + " (" + getDurationMinutes() + " mins)");
        System.out.println("Venue        : " + venue);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getSummary() {
        return String.format("%s [%s - %s @ %s]", title, startTime, endTime, venue);
    }

    @Override
    public String toString() {
        return title + " (" + startTime + ")";
    }

    
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { setScheduleTimes(startTime, this.endTime); }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { setScheduleTimes(this.startTime, endTime); }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
}