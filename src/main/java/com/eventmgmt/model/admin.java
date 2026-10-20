package com.eventmgmt.model;
public class Admin extends User {
    private String username;

    
    public Admin() {
        super();
        this.username = "admin";
    }

    
    public Admin(String id, String username, String password, String name, String email) {
        // Mapping parameters to the inherited User superclass structure
        super(id, name, email, "", password);
        this.username = (username != null && !username.trim().isEmpty()) ? username.trim() : "admin";
    }

    

    @Override
    public String getRoleDescription() {
        return "System Administrator";
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Admin ID       : " + getId());
        System.out.println("Username       : " + username);
        System.out.println("Full Name      : " + getName());
        System.out.println("Email Address  : " + getEmail());
        System.out.println("Role           : " + getRoleDescription());
        System.out.println("----------------------------------------------");
    }

    @Override
    public String toString() {
        return getName() + " (@" + username + ")";
    }

    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username.trim();
        }
    }
}