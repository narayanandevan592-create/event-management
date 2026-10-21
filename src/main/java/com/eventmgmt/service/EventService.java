package com.eventmgmt.service;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.SessionDAO;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Session;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventService{
    private final EventDAO eventDAO;
    private final SessionDAO sessionDAO;

    public EventService(EventDAO eventDAO, SessionDAO sessionDAO){
        this.eventDAO = eventDAO;
        this.sessionDAO = sessionDAO;
    }

   
    public Event createEvent(Event event){
        validateEvent(event);
        eventDAO.save(event);
        return event;
    }

    public List<Event> getAllEvents(){
        List<Event> events = eventDAO.findAll();
        if (sessionDAO != null) {
            for (int i = 0; i < events.size(); i++){
                Event event = events.get(i);
                List<Session> sessions = sessionDAO.findByEventId(event.getEventId());
                event.setSessions(sessions);
            }
        }
        return events;
    }

    public Event getEventById(String eventId){
        if (eventId == null || eventId.trim().isEmpty()){
            return null;
        }
        Event event = eventDAO.findById(eventId.trim());
        if (event != null && sessionDAO != null) {
            List<Session> sessions = sessionDAO.findByEventId(eventId.trim());
            event.setSessions(sessions);
        }
        return event;
    }

    public boolean updateEvent(Event event){
        validateEvent(event);
        return eventDAO.update(event);
    }

    public boolean deleteEvent(String eventId){
        if (eventId == null || eventId.trim().isEmpty()){
            throw new ValidationException("Event ID cannot be empty for deletion.");
        }
        return eventDAO.delete(eventId.trim());
    }

    public List<Event> searchEvents(String keyword){
        if (keyword == null || keyword.trim().isEmpty()){
            return getAllEvents();
        }
        return eventDAO.findByTitle(keyword.trim());
    }

    public List<Event> searchEvents(String keyword, LocalDate date){
        if (date == null){
            return searchEvents(keyword);
        }
        List<Event> matches = searchEvents(keyword);
        List<Event> filtered = new ArrayList<Event>();
        for (int i = 0; i < matches.size(); i++) {
            Event event = matches.get(i);
            if (event.getDate() != null && event.getDate().equals(date)){
                filtered.add(event);
            }
        }
        return filtered;
    }

    private void validateEvent(Event event){
        if (event == null) {
            throw new ValidationException("Event object cannot be null.");
        }
        if (event.getEventId() == null || event.getEventId().trim().isEmpty()){
            throw new ValidationException("Event ID is required.");
        }
        if (event.getEventName() == null || event.getEventName().trim().isEmpty()){
            throw new ValidationException("Event Name is required and cannot be blank.");
        }
        if (event.getDate() == null){
            throw new ValidationException("Event Date is required.");
        }
        if (event.getVenue() == null || event.getVenue().trim().isEmpty()){
            throw new ValidationException("Venue is required.");
        }
        if (event.getOrganizerName() == null || event.getOrganizerName().trim().isEmpty()){
            throw new ValidationException("Organizer Name is required.");
        }
        if (event.getRegistrationFee() < 0.0){
            throw new ValidationException("Registration Fee cannot be negative.");
        }
    }
}