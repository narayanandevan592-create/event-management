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

    // Parameterized Constructor (Constructor Overloading)
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
            throw new InvalidSessionTimeException("Start time and end time cannot be null or empty.");
        }
        // Simple string/lexicographical check or format validation appropriate for basic Java
        if (end.compareTo(start) <= 0) {
            throw new InvalidSessionTimeException(
                "End time (" + end + ") must be strictly later than start time (" + start + ")."
            );
        }
        this.startTime = start;
        this.endTime = end;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Session title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Session date cannot be null or empty.");
        }
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        setScheduleTimes(startTime, this.endTime != null ? this.endTime : "23:59");
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        setScheduleTimes(this.startTime != null ? this.startTime : "00:00", endTime);
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    @Override
    public void displaySchedule() {
        System.out.println(String.format("Session [%s] %s | %s to %s at %s",
                sessionId, title, startTime, endTime, venue));
    }

    @Override
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;
        try {
            
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            
            return Math.max(0, endMinutes - startMinutes);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Session ID   : " + sessionId);
        System.out.println("Title        : " + title);
        System.out.println("Event ID     : " + eventId);
        System.out.println("Date         : " + date);
        System.out.println("Time         : " + startTime + " - " + endTime +
                " (" + getDurationMinutes() + " mins)");
        System.out.println("Venue        : " + venue);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getSummary() {
        return String.format("%s (%s - %s @ %s)", title, 
                startTime != null ? startTime : "N/A",
                endTime != null ? endTime : "N/A", 
                venue);
    }

    @Override
    public int compareTo(Session other) {
        if (other == null) return 1;
        // First compare date
        int dateCmp = this.date.compareTo(other.date);
        if (dateCmp != 0) return dateCmp;
        // Then compare start time
        return this.startTime.compareTo(other.startTime);
    }

    @Override
    public String toString() {
        return title + " [" + (startTime != null ? startTime : "") + "]";
    }
}