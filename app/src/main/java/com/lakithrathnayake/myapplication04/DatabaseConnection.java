package com.lakithrathnayake.myapplication04;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private static Connection connection;
    private DatabaseConnection() {
        try {
            String host = "sql12.freesqldatabase.com:3306";
            String database = "sql12763891";
            String user = "sql12763891";
            String password = "dKqb8rXspg";
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = "jdbc:mysql://" + host + "/" + database + "?allowPublicKeyRetrieval=true";

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            connection = DriverManager.getConnection(dbUrl, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        return (instance == null) ? (instance = new DatabaseConnection()) : instance;
    }

    public Connection getConnection(){return connection;};
}
