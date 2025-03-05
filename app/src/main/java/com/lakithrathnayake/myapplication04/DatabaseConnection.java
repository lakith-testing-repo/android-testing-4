package com.lakithrathnayake.myapplication04;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

//    private static final String host = "34.134.230.151";
    private static final String host = "sql12.freesqldatabase.com";
    private static final String database = "sql12764610";
    private static final String user = "sql12764610";
    private static final String password = "FtApBXFdqA";
    private static final String dbUrl = "jdbc:mysql://" + host + "/" + database + "?allowPublicKeyRetrieval=true";
//    private static final String dbUrl = "jdbc:jtds:sqlserver://" + host + ";databaseName=" + database;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, user, password);
        } catch (Exception e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}
