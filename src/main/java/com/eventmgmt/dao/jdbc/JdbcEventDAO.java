package com.eventmgmt.dao.jdbc;
import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.exception.DatabaseException;
import com.eventmgmt.model.Event;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcEventDAO implements EventDAO {

    @Override
    public void save(Event event) {
        String sql = "INSERT INTO events (event_id, event_name, description, event_date, venue, organizer_name, registration_fee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Try-with-resources automatically closes connections and statements safely
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getEventId());
            statement.setString(2, event.getEventName());
            statement.setString(3, event.getDescription());
            statement.setString(4, event.getDate()); // Using standard String representation for syllabus compatibility
            statement.setString(5, event.getVenue());
            statement.setString(6, event.getOrganizerName());
            statement.setDouble(7, event.getRegistrationFee());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save event '" + event.getEventName() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Event findById(String eventId) {
        String sql = "SELECT event_id, event_name, description, event_date, venue, organizer_name, registration_fee " +
                     "FROM events WHERE event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToEvent(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query event by ID (" + eventId + "): " + e.getMessage(), e);
        }
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT event_id, event_name, description, event_date, venue, organizer_name, registration_fee " +
                     "FROM events ORDER BY event_date ASC, event_name ASC";
        List<Event> eventList = new ArrayList<Event>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                eventList.add(mapRowToEvent(resultSet));
            }
            return eventList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all events: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Event> findByTitle(String titleKeyword) {
        String sql = "SELECT event_id, event_name, description, event_date, venue, organizer_name, registration_fee " +
                     "FROM events WHERE LOWER(event_name) LIKE ? ORDER BY event_date ASC";
        List<Event> eventList = new ArrayList<Event>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + titleKeyword.toLowerCase().trim() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    eventList.add(mapRowToEvent(resultSet));
                }
            }
            return eventList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search events by keyword: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Event> findByDate(String date) {
        String sql = "SELECT event_id, event_name, description, event_date, venue, organizer_name, registration_fee " +
                     "FROM events WHERE event_date = ? ORDER BY event_name ASC";
        List<Event> eventList = new ArrayList<Event>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    eventList.add(mapRowToEvent(resultSet));
                }
            }
            return eventList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find events by date: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Event event) {
        String sql = "UPDATE events SET event_name = ?, description = ?, event_date = ?, venue = ?, organizer_name = ?, registration_fee = ? " +
                     "WHERE event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getEventName());
            statement.setString(2, event.getDescription());
            statement.setString(3, event.getDate());
            statement.setString(4, event.getVenue());
            statement.setString(5, event.getOrganizerName());
            statement.setDouble(6, event.getRegistrationFee());
            statement.setString(7, event.getEventId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update event '" + event.getEventId() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String eventId) {
        // ON DELETE CASCADE in the database schema automatically cleans up associated sessions and registrations
        String sql = "DELETE FROM events WHERE event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete event '" + eventId + "': " + e.getMessage(), e);
        }
    }

    @Override
    public int getRegistrationCount(String eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count registrations for event: " + e.getMessage(), e);
        }
    }

    
    private Event mapRowToEvent(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("event_id");
        String name = resultSet.getString("event_name");
        String description = resultSet.getString("description");
        String date = resultSet.getString("event_date");
        String venue = resultSet.getString("venue");
        String organizer = resultSet.getString("organizer_name");
        
        double fee = 0.0;
        try {
            fee = resultSet.getDouble("registration_fee");
        } catch (SQLException ignored) {
        }

        return new Event(id, name, description, date, venue, organizer, fee);
    }
}