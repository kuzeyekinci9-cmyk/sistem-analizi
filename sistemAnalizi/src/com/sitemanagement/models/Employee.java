package com.sitemanagement.models;

import com.sitemanagement.enums.Role;
// Sitede oturmayan Yönetici veya Güvenlik görevlileri için
public class Employee extends User {

    public Employee(int id, String fullName, String phone, String password, Role role) {
        super(id, fullName, phone, password, role);
    }
}