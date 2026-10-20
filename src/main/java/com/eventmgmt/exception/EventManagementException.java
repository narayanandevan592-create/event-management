package com.eventmgmt.exception;
public class EventManagementException extends RuntimeException {
    public EventManagementException(String message) {
        super(message);
    }
    public EventManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}