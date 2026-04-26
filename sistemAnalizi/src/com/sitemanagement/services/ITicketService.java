package com.sitemanagement.services;

import com.sitemanagement.models.MaintenanceTicket;
import com.sitemanagement.enums.TicketStatus;
import java.util.List;

public interface ITicketService {
    boolean createTicket(int residentId, String title, String description);
    boolean updateTicketStatus(int ticketId, TicketStatus newStatus);
    MaintenanceTicket getTicketById(int ticketId);
    List<MaintenanceTicket> getTicketsByStatus(TicketStatus status);
    List<MaintenanceTicket> getTicketsByResident(int residentId);
}