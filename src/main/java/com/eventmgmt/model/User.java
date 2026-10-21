package com.eventmgmt.model;

import com.eventmgmt.model.interfaces.Displayable;


public abstract class User implements Displayable {
    protected String id; // Accessible to derived subclasses
    private String name;
    private String email;
    private String phone;
    private String password;

    
    public User() {
        this.id = "";
        this.name = "Unknown";
        this.email = "unknown@example.com";
        this.phone = "";
        this.password = "";
    }

   
    public User(String id, String name, String email, String phone) {
        this(id, name, email, phone, "");
    }

    
    public User(String id, String name, String email, String phone, String password) {
        this.id = id;
        setName(name);
        setEmail(email);
        this.phone = (phone != null) ? phone.trim() : "";
        this.password = (password != null) ? password.trim() : "";
    }

    
    public abstract String getRoleDescription();

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.email = email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password != null) ? password.trim() : "";
    }


    @Override
    public String getSummary() {
        return String.format("[%s] %s (%s)", getRoleDescription(), name, email);
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}