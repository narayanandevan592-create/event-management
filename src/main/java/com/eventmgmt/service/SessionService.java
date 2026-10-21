package com.eventmgmt.service;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.SessionDAO;
import com.eventmgmt.exception.InvalidSessionTimeException;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionService{
    private final SessionDAO sessionDAO;
    private final EventDAO eventDAO;

    public SessionService(SessionDAO sessionDAO, EventDAO eventDAO){
        this.sessionDAO = sessionDAO;
        this.eventDAO = eventDAO;
    }

    public Session createSession(Session session){
        validateSession(session);
        sessionDAO.save(session);
        return session;
    }

    public List<Session> getSessionsByEventId(String eventId){
        if (eventId == null || eventId.trim().isEmpty()){
            return new ArrayList<Session>();
        }
        List<Session> sessions = sessionDAO.findByEventId(eventId.trim());
        Collections.sort(sessions); 
        return sessions;
    }

    public Session getSessionById(String sessionId){
        if (sessionId == null || sessionId.trim().isEmpty()){
            return null;
        }
        return sessionDAO.findById(sessionId.trim());
    }

    public boolean updateSession(Session session){
        validateSession(session);
        return sessionDAO.update(session);
    }

    public boolean deleteSession(String sessionId){
        if (sessionId == null || sessionId.trim().isEmpty()){
            throw new ValidationException("Session ID is required for deletion.");
        }
        return sessionDAO.delete(sessionId.trim());
    }

    public String getFormattedSchedule(String eventId){
        Event event = eventDAO.findById(eventId);
        String eventTitle = (event != null) ? event.getEventName() : "Event " + eventId;

        List<Session> sessions = getSessionsByEventId(eventId);
        if (sessions.isEmpty()) {
            return "No scheduled sessions found for " + eventTitle + ".";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                 CHRONOLOGICAL SCHEDULE: ").append(eventTitle.toUpperCase()).append("\n");
        sb.append("========================================================================\n\n");

        for (int i = 0; i < sessions.size(); i++){
            Session session = sessions.get(i);
            sb.append("Session #").append(i + 1).append(": ").append(session.getTitle()).append("\n");
            sb.append("  Date & Time : ").append(session.getDate()).append(" | ")
              .append(session.getStartTime()).append(" - ").append(session.getEndTime())
              .append(" (").append(session.getDurationMinutes()).append(" mins)\n");
            sb.append("  Room/Venue  : ").append(session.getVenue()).append("\n");
            sb.append("------------------------------------------------------------------------\n");
        }
        return sb.toString();
    }

    private void validateSession(Session session){
        if (session == null) {
            throw new ValidationException("Session cannot be null.");
        }
        if (session.getSessionId() == null || session.getSessionId().trim().isEmpty()){
            throw new ValidationException("Session ID is required.");
        }
        if (session.getEventId() == null || session.getEventId().trim().isEmpty()){
            throw new ValidationException("Associated Event is required.");
        }
        if (session.getTitle() == null || session.getTitle().trim().isEmpty()){
            throw new ValidationException("Session title is required.");
        }
        if (session.getDate() == null) {
            throw new ValidationException("Session date is required.");
        }
        if (session.getStartTime() == null || session.getEndTime() == null){
            throw new ValidationException("Both start time and end time are required.");
        }
        if (session.getEndTime().compareTo(session.getStartTime()) <= 0){
            throw new InvalidSessionTimeException(
                "End time (" + session.getEndTime() + ") cannot be earlier than or equal to Start time (" + session.getStartTime() + ")."
            );
        }
        if (session.getVenue() == null || session.getVenue().trim().isEmpty()){
            throw new ValidationException("Session venue / room is required.");
        }
    }
}