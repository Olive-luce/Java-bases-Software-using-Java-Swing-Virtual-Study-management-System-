package com.mycompany.vsp2.dao;

import com.mycompany.vsp2.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Create new user (core method)
    public static boolean createUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1)); // Save generated ID
                }
            }

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Wrapper used by UI that expects SQLException
    public static boolean insert(User user) throws SQLException {
        if (!createUser(user)) {
            throw new SQLException("Insert failed");
        }
        return true;
    }

    // Wrapper used by RegisterPage
    public static boolean register(User user) {
        return createUser(user);
    }

    // Login lookup
    public static User findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role FROM users WHERE email = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // List all users
    public static List<User> listAll() {
        List<User> out = new ArrayList<>();
        String sql = "SELECT id, name, email, password, role FROM users";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return out;
    }

    // Alias expected by UI
    public static List<User> findAll() {
        return listAll();
    }

    // Delete a user
    public static boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // Fetch users with role=student
    public static List<User> findStudents() {
        List<User> out = new ArrayList<>();
        String sql = "SELECT id, name, email, password, role FROM users WHERE role = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, User.ROLE_STUDENT);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    ));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return out;
    }
}
