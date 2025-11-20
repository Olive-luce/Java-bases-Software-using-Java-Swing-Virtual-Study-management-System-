package com.mycompany.vsp2.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    // Upload a new material to a class
    public static void uploadMaterial(int classId, String title, String filePath) throws SQLException {
        String sql = "INSERT INTO materials (classId, title, filePath) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, classId);
            ps.setString(2, title);
            ps.setString(3, filePath);
            ps.executeUpdate();
        }
    }

    // Get all materials for a class — returns "title | path" just like StudentDashboard needs
    public static List<String> getMaterialsByClass(int classId) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT title, filePath FROM materials WHERE classId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String filePath = rs.getString("filePath");
                    out.add(title + " | " + filePath);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return out;
    }
}
