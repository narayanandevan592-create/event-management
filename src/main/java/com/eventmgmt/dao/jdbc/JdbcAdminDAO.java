package com.eventmgmt.dao.jdbc;
import com.eventmgmt.dao.AdminDAO;
import com.eventmgmt.exception.DatabaseException;
import com.eventmgmt.model.Admin;
import com.eventmgmt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcAdminDAO implements AdminDAO {

    @Override
    public Admin authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String sql = "SELECT admin_id, username, password, full_name, email FROM admins " +
                     "WHERE LOWER(username) = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username.trim().toLowerCase());
            statement.setString(2, password.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToAdmin(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Database error during admin authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public Admin findByUsername(String username) {
        if (username == null) {
            return null;
        }

        String sql = "SELECT admin_id, username, password, full_name, email FROM admins WHERE LOWER(username) = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username.trim().toLowerCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToAdmin(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Admin admin) {
        String sql = "INSERT INTO admins (admin_id, username, password, full_name, email) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE password = VALUES(password), full_name = VALUES(full_name), email = VALUES(email)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, admin.getId());
            statement.setString(2, admin.getUsername());
            statement.setString(3, admin.getPassword());
            statement.setString(4, admin.getName());
            statement.setString(5, admin.getEmail());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save admin account: " + e.getMessage(), e);
        }
    }

    
    private Admin mapRowToAdmin(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("admin_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String fullName = resultSet.getString("full_name");
        String email = resultSet.getString("email");

        return new Admin(id, username, password, fullName, email);
    }
}