package com.example.parkwise;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String JDBC_URL = "jdbc:mysql://seniordesign.cebxcsadv7hz.us-west-1.rds.amazonaws.com:3306/ParkWise";
    private static final String USERNAME = "CodeMaster";
    private static final String PASSWORD = "OmegaLambda246";
    private Connection databaseConnection;

    public DatabaseConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            databaseConnection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (databaseConnection == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
        return databaseConnection;
    }

    public void closeDatabaseConnection() {
        try {
            if (databaseConnection != null) {
                databaseConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

