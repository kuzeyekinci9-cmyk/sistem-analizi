package com.sitemanagement.managers;

import com.sitemanagement.services.IParkingService;

public class ParkingManager implements IParkingService{

	@Override
	public boolean createGuestPass(int residentId, String guestPlate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logGuestEntry(String plate, int visitingApartmentId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logExit(String plate) {
		// TODO Auto-generated method stub
		return false;
	}

}
