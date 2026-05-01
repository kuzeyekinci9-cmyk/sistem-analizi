package com.sitemanagement.models;

import com.sitemanagement.enums.Role;

public class Resident extends User {
    private Apartment apartment;

    // 1. Constructor
    public Resident(int id, String fullName, String phone, String password, Role role) {
        super(id, fullName, phone, password, role); // Üst sınıfın constructor'ını çağırıyoruz
    }

    // Getters and Setters
    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }
}