package com.sitemanagement.services;

import com.sitemanagement.models.Resident;
import com.sitemanagement.models.Apartment;
import java.util.List;

public interface IResidentService {
    boolean registerResident(Resident resident);

    boolean removeResident(int residentId);

    boolean updateResident(Resident resident);

    boolean assignToApartment(int residentId, int apartmentId);

    Resident getResidentById(int residentId);

    List<Resident> getAllResidents();

    List<Apartment> getEmptyApartments();
}