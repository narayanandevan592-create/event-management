package com.eventmgmt;
import com.eventmgmt.dao.DAOFactory;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.Participant;
import com.eventmgmt.model.Registration;
import com.eventmgmt.model.Session;
import com.eventmgmt.service.EventService;
import com.eventmgmt.service.ParticipantService;
import com.eventmgmt.service.RegistrationService;
import com.eventmgmt.service.SessionService;
import com.eventmgmt.exception.DuplicateRegistrationException;
import com.eventmgmt.exception.InvalidSessionTimeException;

import java.util.List;


public class SystemIntegrationTest {

    public static void main(String[] args) {
        System.out.println("--- STARTING SYSTEM INTEGRATION TEST ---");

        DAOFactory factory = DAOFactory.getInstance();
        System.out.println("Storage Engine: " + factory.getStorageModeDescription());

        EventService eventService = new EventService(factory.getEventDAO(), factory.getSessionDAO());
        ParticipantService participantService = new ParticipantService(factory.getParticipantDAO());
        SessionService sessionService = new SessionService(factory.getSessionDAO(), factory.getEventDAO());
        RegistrationService regService = new RegistrationService(factory.getRegistrationDAO(), factory.getEventDAO(), factory.getParticipantDAO());

        
        String testEventId = "TEST-EVT-01";
        eventService.deleteEvent(testEventId); 
        
        Event event = new Event(testEventId, "Tech Symposium 2026", "Annual Tech Fest", "2026-11-20", "Main Auditorium", "CS Department");
        eventService.createEvent(event);
        
        Event fetchedEvent = eventService.getEventById(testEventId);
        assert fetchedEvent != null : "Event retrieval failed!";
        System.out.println("[PASS] Feature 1: Event created and retrieved: " + fetchedEvent.getEventName());

       
        String testPartId = "TEST-PAR-01";
        participantService.deleteParticipant(testPartId); 
        
        Participant participant = new Participant(testPartId, "Rahul Menon", "rahul.test@example.com", "9876543210");
        participantService.addParticipant(participant);
        System.out.println("[PASS] Feature 2a: Participant added: " + participant.getName());

        Registration registration = regService.registerParticipant(testEventId, testPartId);
        System.out.println("[PASS] Feature 2b: Registered participant with ID: " + registration.getRegistrationId());

        boolean caughtDuplicate = false;
        try {
            regService.registerParticipant(testEventId, testPartId);
        } catch (DuplicateRegistrationException e) {
            caughtDuplicate = true;
            System.out.println("[PASS] Feature 2c: Duplicate registration correctly caught: " + e.getMessage());
        }
        assert caughtDuplicate : "Duplicate registration was not caught!";

        
        String testSesId = "TEST-SES-01";
        Session session = new Session(testSesId, testEventId, "Opening Keynote", "2026-11-20", "09:30", "11:00", "Hall A");
        sessionService.createSession(session);
        
        List<Session> sessions = sessionService.getSessionsByEventId(testEventId);
        assert !sessions.isEmpty() : "Session retrieval failed!";
        System.out.println("[PASS] Feature 3a: Session created and linked: " + session.getTitle());

        boolean caughtInvalidTime = false;
        try {
            
            Session badSession = new Session("TEST-SES-BAD", testEventId, "Bad Timing", "2026-11-20", "14:00", "13:00", "Hall B");
            sessionService.createSession(badSession);
        } catch (InvalidSessionTimeException e) {
            caughtInvalidTime = true;
            System.out.println("[PASS] Feature 3b: Invalid session time correctly caught: " + e.getMessage());
        }
        assert caughtInvalidTime : "Invalid session time was not caught!";

       
        eventService.deleteEvent(testEventId);
        participantService.deleteParticipant(testPartId);
        System.out.println("[PASS] Cleaned up temporary test records successfully.");

        System.out.println("\n*** ALL SYSTEM INTEGRATION TESTS PASSED SUCCESSFULLY! ***");
    }
}