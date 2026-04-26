package com.sitemanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    
    // SSL ve Timezone sorunlarını çözmek için parametreler eklendi
    private static final String URL = "jdbc:mysql://localhost:3306/site_management?useSSL=false&serverTimezone=UTC"; 
    private static final String USER = "root"; 
    
    // Şifreyi kendi lokal ortamına göre güncelle
    private static final String PASSWORD = "Abg2004."; 

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Sistem: Veritabanı bağlantısı BAŞARILI!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("HATA: MySQL Driver bulunamadı!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("HATA: Veritabanına bağlanılamadı!");
            e.printStackTrace();
        }
        return connection;
    }
}