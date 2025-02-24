package com.lakithrathnayake.myapplication04;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String host = "sql12.freesqldatabase.com:3306";
    private static final String database = "sql12763891";
    private static final String user = "sql12763891";
    private static final String password = "dKqb8rXspg";
    private static final String dbUrl = "jdbc:mysql://" + host + "/" + database + "?allowPublicKeyRetrieval=true";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, user, password);
        } catch (Exception e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}
