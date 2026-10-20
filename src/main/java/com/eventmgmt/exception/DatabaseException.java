package com.eventmgmt.exception;
public class DatabaseException extends EventManagementException {
    public DatabaseException(String message) {
        super(message);
    }
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}