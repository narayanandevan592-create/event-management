package com.eventmgmt.model;


public class Organizer extends User {
    private String organizationName;

    
    public Organizer() {
        super();
        this.organizationName = "General Organization";
    }

    
    public Organizer(String id, String name, String email, String phone, String organizationName) {
        super(id, name, email, phone);
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public String getRoleDescription() {
        return "Event Organizer";
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Organizer ID   : " + getId());
        System.out.println("Full Name      : " + getName());
        System.out.println("Email Address  : " + getEmail());
        System.out.println("Phone Number   : " + getPhone());
        System.out.println("Organization   : " + organizationName);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String toString() {
        return getName() + " [" + organizationName + "]";
    }
}