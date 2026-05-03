package com.sitemanagement.managers;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.enums.TransactionType;
import com.sitemanagement.models.Transaction;
import com.sitemanagement.services.IFinanceService;

public class FinanceManager implements IFinanceService {

    @Override
    public boolean addDebtToResident(int residentId, BigDecimal amount, TransactionType type, String description) {
        // Hangi kolonu güncelleyeceğimizi seçiyoruz
        String column = (type == TransactionType.EXTRA_FEE) ? "extra_debt" : "dues_debt";
        String updateQuery = "UPDATE Users SET " + column + " = " + column + " + ? WHERE id = ?";
        String logQuery = "INSERT INTO Transactions (resident_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    PreparedStatement logStmt = conn.prepareStatement(logQuery)) {

                updateStmt.setBigDecimal(1, amount);
                updateStmt.setInt(2, residentId);
                updateStmt.executeUpdate();

                logStmt.setInt(1, residentId);
                logStmt.setBigDecimal(2, amount);
                logStmt.setString(3, type.toString());
                logStmt.setString(4, description);
                logStmt.executeUpdate();

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
    public List<Transaction> getResidentLedger(int residentId) {
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM Transactions WHERE resident_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getInt("id"), rs.getInt("resident_id"), rs.getBigDecimal("amount"),
                            TransactionType.valueOf(rs.getString("transaction_type")), rs.getString("description")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean logManualPayment(int residentId, BigDecimal amount, String note) {
        // Mevcut borç kontrolü.
        BigDecimal currentDebt = calculateTotalDebt(residentId);
        if (amount.compareTo(currentDebt) > 0) {
            return false; // Fazla ödeme engellendi.
        }

        String updateQuery = "UPDATE Users SET dues_debt = dues_debt - ? WHERE id = ?";
        String logQuery = "INSERT INTO Transactions (resident_id, amount, transaction_type, description) VALUES (?, ?, 'PAYMENT', ?)";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    PreparedStatement logStmt = conn.prepareStatement(logQuery)) {
                updateStmt.setBigDecimal(1, amount);
                updateStmt.setInt(2, residentId);
                updateStmt.executeUpdate();

                logStmt.setInt(1, residentId);
                logStmt.setBigDecimal(2, amount);
                logStmt.setString(3, note);
                logStmt.executeUpdate();

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

    public boolean addSiteFinanceRecord(String type, BigDecimal amount, String description) {
        String query = "INSERT INTO Site_Finances (finance_type, amount, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setBigDecimal(2, amount);
            stmt.setString(3, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gelir-Gider raporu oluşturur.
    @Override
    public List<Transaction> getSiteGeneralReport() {
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM Transactions ORDER BY transaction_date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Transaction(rs.getInt("id"), rs.getInt("resident_id"), rs.getBigDecimal("amount"),
                        TransactionType.valueOf(rs.getString("transaction_type")), rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public BigDecimal calculateTotalDebt(int residentId) {
        String query = "SELECT dues_debt + extra_debt as total FROM Users WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getBigDecimal("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    //Tüm sakinlere toplu borç yansıtır
    public boolean addBulkDebtToAllResidents(BigDecimal amount, TransactionType type, String description) {
        String column = (type == TransactionType.EXTRA_FEE) ? "extra_debt" : "dues_debt";
        
        // 1. Tüm sakinlerin borcunu güncelle
        String updateQuery = "UPDATE Users SET " + column + " = " + column + " + ? WHERE role = 'RESIDENT'";
        
        String logQuery = "INSERT INTO Transactions (resident_id, amount, transaction_type, description) " +
                          "SELECT id, ?, ?, ? FROM Users WHERE role = 'RESIDENT'";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                 PreparedStatement logStmt = conn.prepareStatement(logQuery)) {

                // Update sorgusunun parametresi
                updateStmt.setBigDecimal(1, amount);
                updateStmt.executeUpdate();

                // Log sorgusunun parametreleri
                logStmt.setBigDecimal(1, amount);
                logStmt.setString(2, type.toString());
                logStmt.setString(3, description);
                logStmt.executeUpdate();

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
}