package com.example.campusfix.model;

import java.io.Serializable;

public class HelpContact implements Serializable {
    private long id;
    private String name;
    private String role;
    private String phone;
    private String email;
    private String location;
    private String description;

    public HelpContact() {
    }

    public HelpContact(long id, String name, String role, String phone, String email, String location, String description) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
