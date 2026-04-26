package com.sitemanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    
    // VERİTABANI ADI: site_management
    private static final String URL = "jdbc:mysql://localhost:3306/site_management"; 
    private static final String USER = "root"; 
    
    // şifreyi buraya yaz (Örn: "1234")
    private static final String PASSWORD = "1234"; 

    private static Connection connection = null;

    // Veritabanı bağlantısını çağır
    public static Connection getConnection() {
        try {
            // Eğer bağlantı yoksa veya kapandıysa yeni bağlantı aç
            if (connection == null || connection.isClosed()) {
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Sistem: Veritabanı bağlantısı BAŞARILI!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("HATA: MySQL Driver bulunamadı!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("HATA: Veritabanına bağlanılamadı! ");
            e.printStackTrace();
        }
        return connection;
    }
}

