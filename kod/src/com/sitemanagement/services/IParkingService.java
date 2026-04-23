package com.sitemanagement.services;

public interface IParkingService {
    // Sakinin kendi panelinden plakayı önceden bildirmesi
    boolean createGuestPass(int residentId, String guestPlate); 
    
    // Güvenliğin kapıda çalıştıracağı metot
    boolean logGuestEntry(String plate, int visitingApartmentId); 
    boolean logExit(String plate);
}