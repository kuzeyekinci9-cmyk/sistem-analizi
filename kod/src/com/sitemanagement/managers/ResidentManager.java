package com.sitemanagement.managers;

import java.util.List;

import com.sitemanagement.models.Apartment;
import com.sitemanagement.models.Resident;
import com.sitemanagement.services.IResidentService;

public class ResidentManager implements IResidentService{

	@Override
	public boolean registerResident(Resident resident) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeResident(int residentId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assignToApartment(int residentId, int apartmentId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Resident getResidentById(int residentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Resident> getAllResidents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Apartment> getEmptyApartments() {
		// TODO Auto-generated method stub
		return null;
	}

}
