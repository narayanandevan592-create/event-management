package com.eventmgmt.exception;
public class DuplicateRegistrationException extends EventManagementException {
    public DuplicateRegistrationException(String participantName, String eventTitle) {
        super("Participant '" + participantName + "' is already registered for '" + eventTitle + "'.");
    }
}