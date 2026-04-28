package com.sitemanagement.test;

import com.sitemanagement.db.DatabaseHelper;
import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        Connection conn = DatabaseHelper.getConnection();
        
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    System.out.println("SUCCESS: Database connection is active!");
                } else {
                    System.out.println("ERROR: Connection is closed.");
                }
            } catch (SQLException e) {
                System.out.println("ERROR: Checking connection state failed.");
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: Could not establish a database connection. Please check DatabaseHelper credentials and MySQL service.");
        }
    }
}
