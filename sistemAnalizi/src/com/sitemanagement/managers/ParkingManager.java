package com.sitemanagement.managers;

import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.models.Vehicle;
import com.sitemanagement.models.VehicleLog;
import com.sitemanagement.services.IParkingService;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParkingManager implements IParkingService {

    private final int MAX_CAPACITY = 150;

    @Override
    public boolean registerVehicle(int apartmentId, String licensePlate) {
        String checkCountQuery = "SELECT COUNT(*) AS total_cars FROM Vehicles WHERE apartment_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkCountQuery)) {
            checkStmt.setInt(1, apartmentId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("total_cars") >= 3) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String query = "INSERT INTO Vehicles (apartment_id, license_plate) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, apartmentId);
            stmt.setString(2, licensePlate.toUpperCase());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Vehicle> getVehiclesByApartment(int apartmentId) {
        List<Vehicle> list = new ArrayList<>();
        String query = "SELECT * FROM Vehicles WHERE apartment_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, apartmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Vehicle(rs.getInt("id"), rs.getInt("apartment_id"), rs.getString("license_plate")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class RegisteredVehicleInfo {
        private String ownerInfo;
        private String licensePlate;
        public RegisteredVehicleInfo(String o, String l) { ownerInfo = o; licensePlate = l; }
        public String getOwnerInfo() { return ownerInfo; }
        public String getLicensePlate() { return licensePlate; }
    }

    public List<RegisteredVehicleInfo> getAllRegisteredVehiclesInfo() {
        List<RegisteredVehicleInfo> list = new ArrayList<>();
        String query = "SELECT a.block_name, a.door_number, GROUP_CONCAT(v.license_plate SEPARATOR ', ') as plates "
                + "FROM Apartments a "
                + "LEFT JOIN Vehicles v ON a.id = v.apartment_id "
                + "GROUP BY a.id, a.block_name, a.door_number";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String plates = rs.getString("plates");
                if (plates == null) plates = "-";
                String info = rs.getString("block_name") + " Blok, No: " + rs.getInt("door_number");
                list.add(new RegisteredVehicleInfo(info, plates));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String findOwnerByPlate(String plate) {
        String query = "SELECT u.full_name, a.block_name, a.door_number FROM Apartments a " +
                "JOIN Users u ON a.resident_id = u.id " +
                "JOIN Vehicles v ON a.id = v.apartment_id WHERE v.license_plate = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, plate.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name") + " (" + rs.getString("block_name") + " Blok, No: "
                        + rs.getInt("door_number") + ")";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Bilinmiyor / Misafir";
    }

    @Override
    public boolean logGuestEntry(String plate, int visitingApartmentId) {
        // Hibrit Mantık: Sadece misafirler burada loglanır. 
        if (getCurrentOccupancy() >= MAX_CAPACITY) return false;
        
        // Eğer bu plaka zaten bir sakine aitse, loglamaya (veya bara eklemeye) gerek yok 
        // çünkü sakinler zaten "Kayıtlı" oldukları için barın içindeler.
        if (isResident(plate)) return false;

        String insertQuery = "INSERT INTO Vehicle_Logs (license_plate, is_guest, entry_time) VALUES (?, TRUE, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, plate.toUpperCase());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean logExit(String plate) {
        String query = "UPDATE Vehicle_Logs SET exit_time = CURRENT_TIMESTAMP WHERE license_plate = ? AND exit_time IS NULL";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, plate.toUpperCase());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCurrentOccupancy() {
        int residentCount = 0;
        int activeGuestCount = 0;

        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Kayıtlı Sakin Araçları
            String resQuery = "SELECT COUNT(*) FROM Vehicles";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(resQuery)) {
                if (rs.next()) residentCount = rs.getInt(1);
            }

            // 2. İçerideki Misafir Araçlar
            String guestQuery = "SELECT COUNT(*) FROM Vehicle_Logs WHERE exit_time IS NULL AND is_guest = TRUE";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(guestQuery)) {
                if (rs.next()) activeGuestCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residentCount + activeGuestCount;
    }

    public boolean isResident(String plate) {
        return !findOwnerByPlate(plate).contains("Bilinmiyor");
    }

    public int getApartmentIdByResidentId(int residentId) {
        String query = "SELECT id FROM Apartments WHERE resident_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean createGuestPass(int residentId, String guestPlate) {
        String query = "INSERT INTO Guest_Passes (resident_id, license_plate) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            stmt.setString(2, guestPlate.toUpperCase());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}