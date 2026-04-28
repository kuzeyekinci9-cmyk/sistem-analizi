package com.sitemanagement.managers;

import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.services.IParkingService;
import java.sql.*;

public class ParkingManager implements IParkingService {

    private final int MAX_CAPACITY = 150; 

    // YENİ EKLENDİ: Araç Tanımlama (Diyagramda var, ER'da tablosu var)
    @Override
    public boolean registerVehicle(int apartmentId, String licensePlate) {
        // En fazla 3 araç kontrolü
        String checkCountQuery = "SELECT COUNT(*) AS total_cars FROM Vehicles WHERE apartment_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkCountQuery)) {
            checkStmt.setInt(1, apartmentId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("total_cars") >= 3) {
                return false; // Maksimum kapasiteye (3) ulaşıldı
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String query = "INSERT INTO Vehicles (apartment_id, license_plate) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, apartmentId);
            stmt.setString(2, licensePlate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Belirli bir dairenin araç plakalarını getir
    public java.util.List<String> getVehiclesByApartment(int apartmentId) {
        java.util.List<String> plates = new java.util.ArrayList<>();
        String query = "SELECT license_plate FROM Vehicles WHERE apartment_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, apartmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                plates.add(rs.getString("license_plate"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return plates;
    }

    // Tum araclar (Admin izleme)
    public static class RegisteredVehicleInfo {
        private String ownerInfo;
        private String licensePlate;
        public RegisteredVehicleInfo(String o, String l) { ownerInfo = o; licensePlate = l; }
        public String getOwnerInfo() { return ownerInfo; }
        public String getLicensePlate() { return licensePlate; }
    }

    public java.util.List<RegisteredVehicleInfo> getAllRegisteredVehiclesInfo() {
        java.util.List<RegisteredVehicleInfo> list = new java.util.ArrayList<>();
        String query = "SELECT a.id as apt_id, a.block_name, a.door_number, GROUP_CONCAT(v.license_plate SEPARATOR ', ') as plates " +
                       "FROM Apartments a " +
                       "LEFT JOIN Vehicles v ON a.id = v.apartment_id " +
                       "GROUP BY a.id, a.block_name, a.door_number";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String plates = rs.getString("plates");
                if (plates == null) { plates = "Yok"; }
                String info = "Blok " + rs.getString("block_name") + ", Daire " + rs.getInt("door_number") + " (Maks 3 Araç)";
                list.add(new RegisteredVehicleInfo(info, plates));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // YENİ EKLENDİ: Araç Sorgulama
    @Override
    public String findOwnerByPlate(String plate) {
        String query = "SELECT u.full_name, a.block_name, a.door_number FROM Users u " +
                       "JOIN Apartments a ON u.apartment_id = a.id " +
                       "JOIN Vehicles v ON a.id = v.apartment_id WHERE v.license_plate = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, plate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name") + " (" + rs.getString("block_name") + " Blok, No: " + rs.getInt("door_number") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Bilinmiyor / Misafir";
    }

    @Override
    public boolean logGuestEntry(String plate, int visitingApartmentId) {
        if (getCurrentOccupancy() >= MAX_CAPACITY) return false;

        String insertQuery = "INSERT INTO Vehicle_Logs (license_plate, is_guest) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, plate);
            stmt.setBoolean(2, true); 
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean logExit(String plate) {
        String query = "UPDATE Vehicle_Logs SET exit_time = CURRENT_TIMESTAMP WHERE license_plate = ? AND exit_time IS NULL";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, plate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getCurrentOccupancy() {
        int total = 0;

        // 1. Sisteme kayıtlı kalıcı araçların sayısı
        String regQuery = "SELECT COUNT(*) AS count_reg FROM Vehicles";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(regQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) { total += rs.getInt("count_reg"); }
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Halihazırda içeride olan (çıkış yapmamış) misafir/loglu araçların sayısı
        String guestQuery = "SELECT COUNT(*) AS count_guest FROM Vehicle_Logs WHERE exit_time IS NULL AND is_guest = TRUE";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(guestQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) { total += rs.getInt("count_guest"); }
        } catch (SQLException e) { e.printStackTrace(); }

        return total;
    }

    @Override 
    public boolean createGuestPass(int residentId, String guestPlate) { 
        String query = "INSERT INTO Guest_Passes (resident_id, license_plate) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            stmt.setString(2, guestPlate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}