package com.eventmgmt.exception;

public class InvalidSessionTimeException extends EventManagementException {

    public InvalidSessionTimeException(String message) {
        super("Invalid Session Timing: " + message);
    }
}
