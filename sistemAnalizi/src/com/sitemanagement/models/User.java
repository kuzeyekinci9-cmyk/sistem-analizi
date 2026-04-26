package com.sitemanagement.models;

import com.sitemanagement.enums.Role;

public abstract class User {
    private int id;
    private String fullName;
    private String phone;
    private String password;
    private Role role;

    public User(int id, String fullName, String phone, String password, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    protected void setRole(Role role) { this.role = role; }
}