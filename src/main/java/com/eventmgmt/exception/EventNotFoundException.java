package com.eventmgmt.exception;
public class EventNotFoundException extends EventManagementException {
          @param 
     
    public EventNotFoundException(String eventId) {
        super("Event not found with ID: " + eventId);
    }
}