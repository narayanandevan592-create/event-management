package com.eventmgmt.model.interfaces;

/**
 * Interface representing components that have schedule information.
 * Enforces timing and scheduling contracts.
 */
public interface Schedulable {
    void displaySchedule();
    long getDurationMinutes();
}
