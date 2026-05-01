package com.sitemanagement.models;

public class Vehicle {
    private int id;
    private int apartmentId;
    private String licensePlate;

    public Vehicle(int id, int apartmentId, String licensePlate) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.licensePlate = licensePlate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
