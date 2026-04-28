package com.sitemanagement.test;

import com.sitemanagement.enums.Role;
import com.sitemanagement.enums.TicketStatus;
import com.sitemanagement.managers.*;
import com.sitemanagement.models.*;

import java.math.BigDecimal;
import java.util.List;

public class CoreLogicTest {

    public static void main(String[] args) {
        System.out.println("Starting Backend Core Logic Tests...");
        boolean allPassed = true;

        ResidentManager rm = new ResidentManager();
        FinanceManager fm = new FinanceManager();
        TicketManager tm = new TicketManager();
        ParkingManager pm = new ParkingManager();
        CommunicationManager cm = new CommunicationManager();

        try {
            // 1. Daire Oluşturma Testi
            System.out.print("Testing Create Apartment... ");
            boolean aptCreated = rm.createApartment("TEST-Z", 1, 99);
            if (aptCreated) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED");
                allPassed = false;
            }

            // 2. Sakin Kayıt Testi
            System.out.print("Testing Register Resident... ");
            Resident testRes = new Resident(0, "Test User", "5551234567", "pass123", Role.RESIDENT);
            boolean resRegistered = rm.registerResident(testRes);
            if (resRegistered && testRes.getId() > 0) {
                System.out.println("PASSED (ID: " + testRes.getId() + ")");
            } else {
                System.out.println("FAILED");
                allPassed = false;
            }

            // 3. Sakin Login Testi
            System.out.print("Testing Authentication... ");
            Resident authUser = rm.authenticateUser("5551234567", "pass123", Role.RESIDENT);
            if (authUser != null && authUser.getId() == testRes.getId()) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED");
                allPassed = false;
            }

            // 4. Daire Atama Testi
            System.out.print("Testing Assign Apartment... ");
            List<Apartment> emptyApts = rm.getEmptyApartments();
            Apartment testApt = emptyApts.stream().filter(a -> a.getBlockName().equals("TEST-Z") && a.getDoorNumber() == 99).findFirst().orElse(null);
            if (testApt != null && testRes.getId() > 0) {
                boolean assigned = rm.assignToApartment(testRes.getId(), testApt.getId());
                if (assigned) System.out.println("PASSED");
                else { System.out.println("FAILED"); allPassed = false; }
            } else {
                System.out.println("SKIPPED (Dependency failed)");
            }

            // 5. Finans (Borç Ekleme) Testi
            System.out.print("Testing Add Debt... ");
            if (testRes.getId() > 0) {
                boolean debtAdded = fm.addDebtToResident(testRes.getId(), new BigDecimal("150.00"), "Test Dues");
                if (debtAdded) System.out.println("PASSED");
                else { System.out.println("FAILED"); allPassed = false; }
            } else {
                System.out.println("SKIPPED (Dependency failed)");
            }

            // 6. Arıza (Ticket) Oluşturma Testi
            System.out.print("Testing Create Ticket... ");
            if (testRes.getId() > 0) {
                boolean ticketCreated = tm.createTicket(testRes.getId(), "Water Leak", "Test ticket description");
                if (ticketCreated) System.out.println("PASSED");
                else { System.out.println("FAILED"); allPassed = false; }
            } else {
                System.out.println("SKIPPED (Dependency failed)");
            }

            // 7. Otopark Kayıt Testi
            System.out.print("Testing Vehicle Registration... ");
            if (testApt != null) {
                boolean vehicleRegistered = pm.registerVehicle(testApt.getId(), "34TEST99");
                if (vehicleRegistered) System.out.println("PASSED");
                else { System.out.println("FAILED"); allPassed = false; }
            } else {
                System.out.println("SKIPPED (Dependency failed)");
            }

            // Cleanup
            System.out.println("\nCleaning up test data...");
            if (testRes.getId() > 0) {
                rm.removeResident(testRes.getId());
                System.out.println("Removed Test Resident.");
            }
            if (testApt != null) {
                // Delete the test apartment manually via JDBC since there's no removeApartment in ResidentManager
                try (java.sql.Connection conn = com.sitemanagement.db.DatabaseHelper.getConnection();
                     java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM Apartments WHERE id = ?")) {
                    stmt.setInt(1, testApt.getId());
                    stmt.executeUpdate();
                    System.out.println("Removed Test Apartment.");
                }
            }
            // Tickets, Vehicles and Transactions are deleted by CASCADE constraints.

            System.out.println("\nOverall Test Result: " + (allPassed ? "ALL PASSED" : "SOME TESTS FAILED"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
