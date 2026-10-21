package com.eventmgmt.exception;

public class EventNotFoundException extends EventManagementException {

    public EventNotFoundException(String eventId) {
        super("Event not found with ID: " + eventId);
    }
}
