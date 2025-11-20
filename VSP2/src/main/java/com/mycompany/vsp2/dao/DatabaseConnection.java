package com.mycompany.vsp2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;

    // Adjust connection string, username, password for your environment
    private static final String URL = "jdbc:mysql://localhost:3306/vsp_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Kksolive1234";

    private DatabaseConnection() { }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("MySQL Driver not found. Add connector/J to classpath.");
                ex.printStackTrace();
                throw new SQLException("JDBC Driver missing", ex);
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
