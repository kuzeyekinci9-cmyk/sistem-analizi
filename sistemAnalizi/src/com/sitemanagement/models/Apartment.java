package com.sitemanagement.models;

public class Apartment {
    private int id;
    private String blockName;
    private int floorNumber;
    private int doorNumber;
    private boolean isOccupied; 
    private int residentId; // ERD: resident_id FK
    private int headcount;  // ERD: headcount
    private String residentName; // Sadece UI'da göstermek için (Veritabanında yok)

    public Apartment(int id, String blockName, int floorNumber, int doorNumber) {
        this.id = id;
        this.blockName = blockName;
        this.floorNumber = floorNumber;
        this.doorNumber = doorNumber;
        this.isOccupied = false; // Varsayılan olarak boş atanır
        this.residentId = 0; // Başlangıçta boş
        this.headcount = 0; // Başlangıçta boş
        this.residentName = "Boş"; // Başlangıçta UI için Boş
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

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }
}