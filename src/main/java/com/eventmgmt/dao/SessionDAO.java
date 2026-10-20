package com.eventmgmt.dao;
import com.eventmgmt.model.Session;
import java.util.List;
public interface SessionDAO {
    void save(Session session);
    Session findById(String sessionId);
    List<Session> findByEventId(String eventId);
    boolean update(Session session);
    boolean delete(String sessionId);
    boolean deleteByEventId(String eventId);
}