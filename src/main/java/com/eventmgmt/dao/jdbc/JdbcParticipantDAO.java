package com.eventmgmt.dao.jdbc;
import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.exception.DatabaseException;
import com.eventmgmt.model.Participant;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcParticipantDAO implements ParticipantDAO {

    @Override
    public void save(Participant participant) {
        String sql = "INSERT INTO participants (participant_id, full_name, email, phone, password) " +
                     "VALUES (?, ?, ?, ?, ?)";

        // Try-with-resources safely manages connection and statement closures
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participant.getId());
            statement.setString(2, participant.getName());
            statement.setString(3, participant.getEmail());
            statement.setString(4, participant.getPhone());
            statement.setString(5, participant.getPassword());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save participant '" + participant.getName() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Participant findById(String participantId) {
        String sql = "SELECT participant_id, full_name, email, phone, password FROM participants WHERE participant_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToParticipant(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find participant by ID (" + participantId + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Participant findByEmail(String email) {
        if (email == null) {
            return null;
        }
        
        String sql = "SELECT participant_id, full_name, email, phone, password FROM participants WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email.trim().toLowerCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToParticipant(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find participant by email: " + e.getMessage(), e);
        }
    }

    @Override
    public Participant authenticate(String emailOrId, String password) {
        if (emailOrId == null || password == null) {
            return null;
        }
        
        String cleanIdentifier = emailOrId.trim().toLowerCase();
        String sql = "SELECT participant_id, full_name, email, phone, password FROM participants " +
                     "WHERE (LOWER(email) = ? OR LOWER(participant_id) = ?) AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cleanIdentifier);
            statement.setString(2, cleanIdentifier);
            statement.setString(3, password.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToParticipant(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Database error during participant authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Participant> findAll() {
        String sql = "SELECT participant_id, full_name, email, phone, password FROM participants ORDER BY full_name ASC";
        List<Participant> participantList = new ArrayList<Participant>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                participantList.add(mapRowToParticipant(resultSet));
            }
            return participantList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch participants: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Participant participant) {
        String sql = "UPDATE participants SET full_name = ?, email = ?, phone = ?, password = ? WHERE participant_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participant.getName());
            statement.setString(2, participant.getEmail());
            statement.setString(3, participant.getPhone());
            statement.setString(4, participant.getPassword());
            statement.setString(5, participant.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update participant: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String participantId) {
        String sql = "DELETE FROM participants WHERE participant_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete participant: " + e.getMessage(), e);
        }
    }


    private Participant mapRowToParticipant(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("participant_id");
        String name = resultSet.getString("full_name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        
        String password = "student123";
        try {
            String dbPass = resultSet.getString("password");
            if (dbPass != null) {
                password = dbPass;
            }
        } catch (SQLException ignored) {
        }

        return new Participant(id, name, email, phone, password);
    }
}