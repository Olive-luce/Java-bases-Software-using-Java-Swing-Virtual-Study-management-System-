package com.mycompany.vsp2.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    // Insert or update marks for a student in a class
    public static void setMarks(int studentId, int classId, int marks) throws SQLException {
        // REPLACE INTO ensures only one marks record per student+class
        String sql = "REPLACE INTO marks (studentId, classId, marks) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, classId);
            ps.setInt(3, marks);
            ps.executeUpdate();
        }
    }

    // Returns rows formatted as: "Class: <name> -> <marks>"
    public static List<String> getMarksByStudent(int studentId) {
        List<String> out = new ArrayList<>();

        String sql = """
            SELECT m.classId, c.className, m.marks
            FROM marks m
            JOIN classroom c ON m.classId = c.classId
            WHERE m.studentId = ?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String className = rs.getString("className");
                    int marks = rs.getInt("marks");
                    out.add("Class: " + className + " -> " + marks);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return out;
    }

    // Computes overall progress: average marks
    public static int getProgress(int studentId) {
        String sql = "SELECT AVG(marks) AS avg_mark FROM marks WHERE studentId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_mark");
                    if (rs.wasNull()) return 0;  // If student has no marks yet
                    return (int) Math.round(avg);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
