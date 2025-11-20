package com.mycompany.vsp2.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    // Create a new class (teacher creates)
    public static void createClass(String className, int teacherId) throws SQLException {
        String sql = "INSERT INTO classroom (className, teacherId) VALUES (?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, className);
            ps.setInt(2, teacherId);
            ps.executeUpdate();
        }
    }

    // List all class names only
    public static List<String> listAll() {
        List<String> out = new ArrayList<>();
        String sql = "SELECT className FROM classroom";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(rs.getString("className"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    // List classes a student has joined
    public static List<String> getClassesByStudent(int studentId) {
        List<String> out = new ArrayList<>();
        String sql =
            "SELECT c.className " +
            "FROM class_students cs " +
            "JOIN classroom c ON cs.classId = c.classId " +
            "WHERE cs.studentId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString("className"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    // List all classes with their IDs (for joining)
    public static List<ClassSummary> listAllWithId() {
        List<ClassSummary> out = new ArrayList<>();
        String sql = "SELECT classId, className FROM classroom";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new ClassSummary(
                    rs.getInt("classId"),
                    rs.getString("className")
                ));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    // Simple DTO
    public static class ClassSummary {
        public final int id;
        public final String name;

        public ClassSummary(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
