package com.sitemanagement.test;

import com.sitemanagement.enums.Role;
import com.sitemanagement.managers.ResidentManager;
import com.sitemanagement.models.Apartment;
import com.sitemanagement.models.Resident;
import java.util.List;

public class SetupResidentDemo {
    public static void main(String[] args) {
        System.out.println("Setting up a Demo Resident for UI Testing...");
        ResidentManager rm = new ResidentManager();

        try {
            // 1. Create an Apartment
            rm.createApartment("DEMO", 1, 1);
            System.out.println("- Created DEMO Block, Apartment No: 1");

            // 2. Create a Resident
            Resident demoUser = new Resident(0, "Demo Sakin", "5551234567", "1234", Role.RESIDENT);
            if (rm.registerResident(demoUser)) {
                System.out.println("- Created Resident: 'Demo Sakin' (Phone: 5551234567, Pass: 1234)");

                // 3. Assign Resident to Apartment
                List<Apartment> apartments = rm.getAllApartments();
                Apartment demoApt = apartments.stream()
                        .filter(a -> a.getBlockName().equals("DEMO") && a.getDoorNumber() == 1)
                        .findFirst()
                        .orElse(null);

                if (demoApt != null && rm.assignToApartment(demoUser.getId(), demoApt.getId())) {
                    System.out.println("- Assigned Resident to DEMO Block Apartment 1");
                    
                    System.out.println("\nSUCCESS! You can now launch MainApp and log in as a Resident.");
                    System.out.println("Role: SAKİN");
                    System.out.println("Telefon: 5551234567");
                    System.out.println("Şifre: 1234");
                } else {
                    System.out.println("Failed to assign apartment.");
                }
            } else {
                System.out.println("Failed to register resident. (Maybe already exists?)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
