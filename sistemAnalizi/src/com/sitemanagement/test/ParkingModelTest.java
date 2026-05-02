package com.sitemanagement.test;

import com.sitemanagement.managers.ParkingManager;
import com.sitemanagement.models.Vehicle;
import java.util.List;

public class ParkingModelTest {
    public static void main(String[] args) {
        System.out.println("=== Otopark Kayıtlı Araç Testi ===");
        ParkingManager pm = new ParkingManager();

        // 1. Manuel Veri Ekleme (Test için)
        int testAptId = 1;
        String testPlate = "34TEST" + System.currentTimeMillis() % 1000;
        
        System.out.println("Test Plakası: " + testPlate);
        boolean success = pm.registerVehicle(testAptId, testPlate);
        System.out.println("Araç Kaydı: " + (success ? "BAŞARILI" : "BAŞARISIZ"));

        // 2. Listeleme Testi
        System.out.println("\nDaire " + testAptId + " için kayıtlı araçlar:");
        List<Vehicle> vehicles = pm.getVehiclesByApartment(testAptId);
        if (vehicles.isEmpty()) {
            System.out.println("-> HATA: Araç listesi boş döndü!");
        } else {
            for (Vehicle v : vehicles) {
                System.out.println("-> Plaka: " + v.getLicensePlate());
            }
        }

        // 3. Sakin Kontrol Testi
        boolean isRes = pm.isResident(testPlate);
        System.out.println("\nPlaka Sakin Aracı mı? " + (isRes ? "EVET (Doğru)" : "HAYIR (Hatalı)"));
    }
}
