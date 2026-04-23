package com.sitemanagement.models;

import com.sitemanagement.enums.Role;
import java.math.BigDecimal;

public class Resident extends User {
    private Apartment apartment;
    private String licensePlate;
    private BigDecimal duesDebt;  // Aidat borcu
    private BigDecimal extraDebt; // Onarım/Tadilat borcu

    // 1. Constructor: Arabası olmayan sakinler için
    public Resident(int id, String fullName, String phone, String password, Role role) {
        super(id, fullName, phone, password, role); // Üst sınıfın constructor'ını çağırıyoruz
        this.licensePlate = null; 
        this.duesDebt = BigDecimal.ZERO;
        this.extraDebt = BigDecimal.ZERO;
    }

    // 2. Constructor (Overloading): Arabası olan sakinler için
    public Resident(int id, String fullName, String phone, String password, Role role, String licensePlate) {
        super(id, fullName, phone, password, role); //
        this.licensePlate = licensePlate;
        this.duesDebt = BigDecimal.ZERO;
        this.extraDebt = BigDecimal.ZERO;
    }
    
    // Toplam borcu dinamik hesaplayan metot
    public BigDecimal getTotalDebt() {
        return duesDebt.add(extraDebt);
    }

    // Getters and Setters
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public BigDecimal getDuesDebt() { return duesDebt; }
    public void setDuesDebt(BigDecimal duesDebt) { this.duesDebt = duesDebt; }

    public BigDecimal getExtraDebt() { return extraDebt; }
    public void setExtraDebt(BigDecimal extraDebt) { this.extraDebt = extraDebt; }

    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }
}