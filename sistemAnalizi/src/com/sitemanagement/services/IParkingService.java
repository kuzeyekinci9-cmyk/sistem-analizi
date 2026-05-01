package com.sitemanagement.services;

public interface IParkingService {
    boolean createGuestPass(int residentId, String guestPlate);

    boolean logGuestEntry(String plate, int visitingApartmentId);

    boolean logExit(String plate);

    boolean registerVehicle(int apartmentId, String licensePlate);

    String findOwnerByPlate(String plate);
}