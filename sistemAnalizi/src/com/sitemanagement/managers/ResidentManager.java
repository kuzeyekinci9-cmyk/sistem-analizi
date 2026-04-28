package com.sitemanagement.managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.enums.Role;
import com.sitemanagement.models.Apartment;
import com.sitemanagement.models.Resident;
import com.sitemanagement.services.IResidentService;

public class ResidentManager implements IResidentService {

    @Override
    public boolean registerResident(Resident resident) {
        String query = "INSERT INTO Users (full_name, phone, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getPhone());
            stmt.setString(3, resident.getPassword());
            stmt.setString(4, resident.getRole().toString());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        resident.setId(generatedKeys.getInt(1)); 
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeResident(int residentId) {
        String query = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateResident(Resident resident) {
        String query = "UPDATE Users SET full_name = ?, phone = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getPhone());
            stmt.setString(3, resident.getPassword());
            stmt.setInt(4, resident.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean assignToApartment(int residentId, int apartmentId) {
        String updateUserQuery = "UPDATE Users SET apartment_id = ? WHERE id = ?";
        String updateAptQuery = "UPDATE Apartments SET is_occupied = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false); // İşlemleri garantiye almak için
            
            try (PreparedStatement stmtUser = conn.prepareStatement(updateUserQuery);
                 PreparedStatement stmtApt = conn.prepareStatement(updateAptQuery)) {
                
                // Sakini daireye bağla
                stmtUser.setInt(1, apartmentId);
                stmtUser.setInt(2, residentId);
                stmtUser.executeUpdate();
                
                // Daireyi 'Dolu' olarak işaretle
                stmtApt.setInt(1, apartmentId);
                stmtApt.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Resident> getAllResidents() {
        List<Resident> list = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE role = 'RESIDENT'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Resident r = new Resident(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone"), rs.getString("password"), Role.RESIDENT);
                r.setDuesDebt(rs.getBigDecimal("dues_debt"));
                r.setExtraDebt(rs.getBigDecimal("extra_debt"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Resident authenticateUser(String phone, String password, Role role) {
        String query = "SELECT * FROM Users WHERE phone = ? AND password = ? AND role = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, phone);
            stmt.setString(2, password);
            stmt.setString(3, role.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Resident user = new Resident(
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("phone"),
                        rs.getString("password"), role
                    );
                    user.setDuesDebt(rs.getBigDecimal("dues_debt"));
                    user.setExtraDebt(rs.getBigDecimal("extra_debt")); // EKSİKTİ EKLENDİ
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override 
    public Resident getResidentById(int residentId) { 
        String query = "SELECT * FROM Users WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt =prepareStatement(query)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Resident r = new Resident(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone"), rs.getString("password"), Role.valueOf(rs.getString("role")));
                r.setDuesDebt(rs.getBigDecimal("dues_debt"));
                r.setExtraDebt(rs.getBigDecimal("extra_debt"));
                return r;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; 
    }

    @Override 
    public List<Apartment> getEmptyApartments() { 
        List<Apartment> list = new ArrayList<>();
        String query = "SELECT * FROM Apartments WHERE is_occupied = FALSE";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()){
                list.add(new Apartment(rs.getInt("id"), rs.getString("block_name"), rs.getInt("floor_number"), rs.getInt("door_number")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean createApartment(String blockName, int floorNumber, int doorNumber) {
        String query = "INSERT INTO Apartments (block_name, floor_number, door_number, is_occupied) VALUES (?, ?, ?, FALSE)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, blockName);
            stmt.setInt(2, floorNumber);
            stmt.setInt(3, doorNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Apartment> getAllApartments() {
        List<Apartment> list = new ArrayList<>();
        String query = "SELECT a.*, GROUP_CONCAT(u.full_name SEPARATOR ', ') AS resident_names FROM Apartments a LEFT JOIN Users u ON a.id = u.apartment_id GROUP BY a.id";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Apartment apt = new Apartment(rs.getInt("id"), rs.getString("block_name"), rs.getInt("floor_number"), rs.getInt("door_number"));
                apt.setOccupied(rs.getBoolean("is_occupied"));
                String names = rs.getString("resident_names");
                apt.setResidentNames(names != null ? names : "Boş");
                list.add(apt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private PreparedStatement prepareStatement(String query) throws SQLException {
        return DatabaseHelper.getConnection().prepareStatement(query);
    }
}