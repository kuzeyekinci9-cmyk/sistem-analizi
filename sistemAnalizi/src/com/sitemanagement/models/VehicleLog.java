package com.sitemanagement.models;

import java.time.LocalDateTime;

public class VehicleLog {
    private int logId;
    private String licensePlate;
    private boolean isGuest;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime; // Çıkış yapana kadar null kalacak

    public VehicleLog(int logId, String licensePlate, boolean isGuest) {
        this.logId = logId;
        this.licensePlate = licensePlate;
        this.isGuest = isGuest;
        this.entryTime = LocalDateTime.now();
    }

    // Araç çıkış yaptığında çağrılacak
    public void markExit() {
        this.exitTime = LocalDateTime.now();
    }

    public int getLogId() {
        return logId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }
}