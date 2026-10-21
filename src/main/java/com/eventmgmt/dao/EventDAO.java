package com.eventmgmt.dao;
import com.eventmgmt.model.Event;
import java.util.List;
public interface EventDAO {
    void save(Event event);
    Event findById(String eventId);
    List<Event> findAll();
    List<Event> findByTitle(String titleKeyword);
    List<Event> findByDate(String date);
    boolean update(Event event);
    boolean delete(String eventId);
    int getRegistrationCount(String eventId);
}