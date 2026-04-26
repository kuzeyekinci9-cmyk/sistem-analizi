package com.sitemanagement.models;

public class Apartment {
    private int id;
    private String blockName;
    private int floorNumber;
    private int doorNumber;
    private boolean isOccupied; 
    private String residentNames; // EKLENDİ: Hangi sakinlerin yaşadığını tutmak için

    public Apartment(int id, String blockName, int floorNumber, int doorNumber) {
        this.id = id;
        this.blockName = blockName;
        this.floorNumber = floorNumber;
        this.doorNumber = doorNumber;
        this.isOccupied = false; // Varsayılan olarak boş atanır
        this.residentNames = ""; // Başlangıçta boş
    }

    // --- Getter ve Setter Metodları ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    // HATA VEREN METODLAR EKLENDİ
    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public String getResidentNames() {
        return residentNames;
    }

    public void setResidentNames(String residentNames) {
        this.residentNames = residentNames;
    }
}