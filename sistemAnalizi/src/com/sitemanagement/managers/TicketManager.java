package com.sitemanagement.managers;

import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.enums.TicketStatus;
import com.sitemanagement.models.MaintenanceTicket;
import com.sitemanagement.services.ITicketService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketManager implements ITicketService {

    @Override
    public boolean createTicket(int residentId, String title, String description) {
        String query = "INSERT INTO Maintenance_Tickets (resident_id, title, description, status) VALUES (?, ?, ?, 'OPEN')";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTicketStatus(int ticketId, TicketStatus newStatus) {
        String query = "UPDATE Maintenance_Tickets SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus.toString());
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MaintenanceTicket> getTicketsByStatus(TicketStatus status) {
        List<MaintenanceTicket> list = new ArrayList<>();
        String query = "SELECT * FROM Maintenance_Tickets WHERE status = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MaintenanceTicket t = new MaintenanceTicket(rs.getInt("id"), rs.getInt("resident_id"),
                            rs.getString("title"), rs.getString("description"));
                    t.setStatus(TicketStatus.valueOf(rs.getString("status")));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MaintenanceTicket> getTicketsByResident(int residentId) {
        List<MaintenanceTicket> list = new ArrayList<>();
        String query = "SELECT * FROM Maintenance_Tickets WHERE resident_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MaintenanceTicket t = new MaintenanceTicket(rs.getInt("id"), rs.getInt("resident_id"),
                            rs.getString("title"), rs.getString("description"));
                    t.setStatus(TicketStatus.valueOf(rs.getString("status")));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean assignTicketToStaff(int ticketId, int staffId) {
        // Personel ID doğrulaması.
        String checkStaffQuery = "SELECT role FROM Users WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkStaffQuery)) {
            checkStmt.setInt(1, staffId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next())
                return false; // Kullanıcı bulunamadı.
            String role = rs.getString("role");
            if ("RESIDENT".equalsIgnoreCase(role))
                return false; // Yetkisiz rol.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Atama işlemi.
        String query = "UPDATE Maintenance_Tickets SET assigned_staff_id = ?, status = 'IN_PROGRESS' WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MaintenanceTicket getTicketById(int ticketId) {
        String query = "SELECT * FROM Maintenance_Tickets WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MaintenanceTicket t = new MaintenanceTicket(rs.getInt("id"), rs.getInt("resident_id"),
                        rs.getString("title"), rs.getString("description"));
                t.setStatus(TicketStatus.valueOf(rs.getString("status")));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}