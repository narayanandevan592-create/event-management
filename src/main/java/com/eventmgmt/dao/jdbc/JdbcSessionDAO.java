package com.eventmgmt.dao.jdbc;
import com.eventmgmt.dao.SessionDAO;
import com.eventmgmt.exception.DatabaseException;
import com.eventmgmt.model.Session;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JdbcSessionDAO implements SessionDAO {

    @Override
    public void save(Session session) {
        String sql = "INSERT INTO sessions (session_id, event_id, session_title, session_date, start_time, end_time, venue) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, session.getSessionId());
            statement.setString(2, session.getEventId());
            statement.setString(3, session.getTitle());
            statement.setString(4, session.getDate());      // Using standard String representation for syllabus compatibility
            statement.setString(5, session.getStartTime()); // Using standard String representation for syllabus compatibility
            statement.setString(6, session.getEndTime());   // Using standard String representation for syllabus compatibility
            statement.setString(7, session.getVenue());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save session '" + session.getTitle() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Session findById(String sessionId) {
        String sql = "SELECT session_id, event_id, session_title, session_date, start_time, end_time, venue " +
                     "FROM sessions WHERE session_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sessionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToSession(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find session by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Session> findByEventId(String eventId) {
        String sql = "SELECT session_id, event_id, session_title, session_date, start_time, end_time, venue " +
                     "FROM sessions WHERE event_id = ? ORDER BY session_date ASC, start_time ASC";
        List<Session> sessionList = new ArrayList<Session>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessionList.add(mapRowToSession(resultSet));
                }
            }
            
            // Sort sessions naturally via the Comparable<Session> interface implementation
            Collections.sort(sessionList);
            return sessionList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch sessions for event (" + eventId + "): " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Session session) {
        String sql = "UPDATE sessions SET session_title = ?, session_date = ?, start_time = ?, end_time = ?, venue = ? " +
                     "WHERE session_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, session.getTitle());
            statement.setString(2, session.getDate());
            statement.setString(3, session.getStartTime());
            statement.setString(4, session.getEndTime());
            statement.setString(5, session.getVenue());
            statement.setString(6, session.getSessionId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update session: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sessionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete session (" + sessionId + "): " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteByEventId(String eventId) {
        String sql = "DELETE FROM sessions WHERE event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete sessions for event: " + e.getMessage(), e);
        }
    }

    private Session mapRowToSession(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("session_id");
        String eventId = resultSet.getString("event_id");
        String title = resultSet.getString("session_title");
        String date = resultSet.getString("session_date");
        String startTime = resultSet.getString("start_time");
        String endTime = resultSet.getString("end_time");
        String venue = resultSet.getString("venue");

        return new Session(id, eventId, title, date, startTime, endTime, venue);
    }
}