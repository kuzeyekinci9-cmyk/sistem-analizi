package com.sitemanagement.managers;

import java.util.List;

import com.sitemanagement.enums.TicketStatus;
import com.sitemanagement.models.MaintenanceTicket;
import com.sitemanagement.services.ITicketService;

public class TicketManager implements ITicketService{

	@Override
	public boolean createTicket(int residentId, String title, String description) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateTicketStatus(int ticketId, TicketStatus newStatus) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MaintenanceTicket getTicketById(int ticketId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MaintenanceTicket> getTicketsByStatus(TicketStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MaintenanceTicket> getTicketsByResident(int residentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
