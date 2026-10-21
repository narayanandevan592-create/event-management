package com.eventmgmt.dao.jdbc;
import com.eventmgmt.dao.RegistrationDAO;
import com.eventmgmt.exception.DatabaseException;
import com.eventmgmt.model.Registration;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class JdbcRegistrationDAO implements RegistrationDAO {

    
    private static final String SELECT_BASE = 
        "SELECT r.registration_id, r.event_id, r.participant_id, r.registration_date, r.status, " +
        "       r.fee_amount, r.payment_mode, r.payment_status, r.payment_notes, " +
        "       e.event_name, p.full_name AS participant_name, p.email AS participant_email " +
        "FROM registrations r " +
        "JOIN events e ON r.event_id = e.event_id " +
        "JOIN participants p ON r.participant_id = p.participant_id ";

    @Override
    public void save(Registration registration) {
        String sql = "INSERT INTO registrations (registration_id, event_id, participant_id, registration_date, status, " +
                     "fee_amount, payment_mode, payment_status, payment_notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, registration.getRegistrationId());
            statement.setString(2, registration.getEventId());
            statement.setString(3, registration.getParticipantId());
            statement.setString(4, registration.getRegistrationDate()); // Using standard String for syllabus alignment
            statement.setString(5, registration.getStatus());
            statement.setDouble(6, registration.getFeeAmount());
            statement.setString(7, registration.getPaymentMode());
            statement.setString(8, registration.getPaymentStatus());
            statement.setString(9, registration.getPaymentNotes());

            statement.executeUpdate();
        } catch (SQLException e) {
            // Check for MySQL duplicate key error code 1062 (preventing duplicate registrations)
            if (e.getErrorCode() == 1062) {
                throw new DatabaseException("Participant is already registered for this event (Duplicate Entry).", e);
            }
            throw new DatabaseException("Failed to save registration: " + e.getMessage(), e);
        }
    }

    @Override
    public Registration findById(String registrationId) {
        String sql = SELECT_BASE + "WHERE r.registration_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, registrationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToRegistration(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registration by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Registration findByEventAndParticipant(String eventId, String participantId) {
        String sql = SELECT_BASE + "WHERE r.event_id = ? AND r.participant_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);
            statement.setString(2, participantId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToRegistration(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to verify existing registration: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Registration> findByEventId(String eventId) {
        String sql = SELECT_BASE + "WHERE r.event_id = ? ORDER BY r.registration_date DESC";
        List<Registration> registrationList = new ArrayList<Registration>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    registrationList.add(mapRowToRegistration(resultSet));
                }
            }
            return registrationList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registrations for event: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Registration> findByParticipantId(String participantId) {
        String sql = SELECT_BASE + "WHERE r.participant_id = ? ORDER BY r.registration_date DESC";
        List<Registration> registrationList = new ArrayList<Registration>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    registrationList.add(mapRowToRegistration(resultSet));
                }
            }
            return registrationList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registrations for participant: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Registration> findAll() {
        String sql = SELECT_BASE + "ORDER BY r.registration_date DESC";
        List<Registration> registrationList = new ArrayList<Registration>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                registrationList.add(mapRowToRegistration(resultSet));
            }
            return registrationList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all registrations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Registration> findPendingPayments() {
        String sql = SELECT_BASE + "WHERE UPPER(r.payment_status) = 'PENDING_APPROVAL' ORDER BY r.registration_date DESC";
        List<Registration> registrationList = new ArrayList<Registration>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                registrationList.add(mapRowToRegistration(resultSet));
            }
            return registrationList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pending payment registrations: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Registration registration) {
        String sql = "UPDATE registrations SET status = ?, registration_date = ?, fee_amount = ?, " +
                     "payment_mode = ?, payment_status = ?, payment_notes = ? WHERE registration_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, registration.getStatus());
            statement.setString(2, registration.getRegistrationDate());
            statement.setDouble(3, registration.getFeeAmount());
            statement.setString(4, registration.getPaymentMode());
            statement.setString(5, registration.getPaymentStatus());
            statement.setString(6, registration.getPaymentNotes());
            statement.setString(7, registration.getRegistrationId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update registration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePaymentStatus(String registrationId, String paymentStatus, String registrationStatus, String notes) {
        String sql = "UPDATE registrations SET payment_status = ?, status = ?, payment_notes = ? WHERE registration_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, paymentStatus);
            statement.setString(2, registrationStatus);
            statement.setString(3, notes != null ? notes : "");
            statement.setString(4, registrationId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update payment status for registration '" + registrationId + "': " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String registrationId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, registrationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete registration: " + e.getMessage(), e);
        }
    }

    private Registration mapRowToRegistration(ResultSet resultSet) throws SQLException {
        String registrationId = resultSet.getString("registration_id");
        String eventId = resultSet.getString("event_id");
        String participantId = resultSet.getString("participant_id");
        String registrationDate = resultSet.getString("registration_date");
        String status = resultSet.getString("status");

        double feeAmount = 0.0;
        String paymentMode = "OFFLINE";
        String paymentStatus = "CONFIRMED";
        String paymentNotes = "";
        
        try {
            feeAmount = resultSet.getDouble("fee_amount");
            paymentMode = resultSet.getString("payment_mode");
            paymentStatus = resultSet.getString("payment_status");
            paymentNotes = resultSet.getString("payment_notes");
        } catch (SQLException ignored) {
        }

        Registration registration = new Registration(
                registrationId, eventId, participantId, registrationDate, status,
                feeAmount, paymentMode, paymentStatus, paymentNotes
        );
        
        registration.setEventName(resultSet.getString("event_name"));
        registration.setParticipantName(resultSet.getString("participant_name"));
        registration.setParticipantEmail(resultSet.getString("participant_email"));
        
        return registration;
    }
}