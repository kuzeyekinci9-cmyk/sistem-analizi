package com.sitemanagement.models;

import com.sitemanagement.enums.TicketStatus;
import java.time.LocalDateTime;

public class MaintenanceTicket {
    private int ticketId;
    private int residentId; // Talebi açan sakin
    private String title;
    private String description;
    private TicketStatus status;
    private LocalDateTime createdAt;

    public MaintenanceTicket(int ticketId, int residentId, String title, String description) {
        this.ticketId = ticketId;
        this.residentId = residentId;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.OPEN; // İlk açıldığında durum her zaman OPEN'dır
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public int getResidentId() { return residentId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
}