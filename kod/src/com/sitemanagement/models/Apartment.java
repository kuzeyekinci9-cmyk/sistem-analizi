package com.sitemanagement.models;

public class Apartment {
    private int apartmentId;
    private String blockName;
    private int floorNumber;
    private int doorNumber;
    private int headcount;
    private boolean isOccupied;
    private Resident resident; // Dairede oturan kişi (Eğer boşsa null olur)

    public Apartment(int apartmentId, String blockName, int floorNumber, int doorNumber) {
        this.apartmentId = apartmentId;
        this.blockName = blockName;
        this.floorNumber = floorNumber;
        this.doorNumber = doorNumber;
        this.isOccupied = false;
        this.headcount = 0;
    }

    // Sakin ataması yapıldığında çalışacak yardımcı metot
    public void assignResident(Resident resident, int headcount) {
        this.resident = resident;
        this.headcount = headcount;
        this.isOccupied = true;
        resident.setApartment(this);
    }

    public void removeResident() {
        if (this.resident != null) {
            this.resident.setApartment(null);
        }
        this.resident = null;
        this.headcount = 0;
        this.isOccupied = false;
    }


    public int getApartmentId() { return apartmentId; }
    public String getBlockName() { return blockName; }
    public int getFloorNumber() { return floorNumber; }
    public int getDoorNumber() { return doorNumber; }
    public int getHeadcount() { return headcount; }
    public boolean isOccupied() { return isOccupied; }
    public Resident getResident() { return resident; }
}