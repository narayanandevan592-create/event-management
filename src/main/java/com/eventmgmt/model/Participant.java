package com.eventmgmt.model;
public class Participant extends User {

    
    public Participant() {
        super();
    }

    
    public Participant(String id, String name, String email, String phone) {
        this(id, name, email, phone, "student123");
    }

   
    public Participant(String id, String name, String email, String phone, String password) {
        super(id, name, email, phone, password);
    }

    
    @Override
    public String getRoleDescription() {
        return "Student Participant";
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------------");
        System.out.println("Participant ID : " + getId());
        System.out.println("Full Name      : " + getName());
        System.out.println("Email Address  : " + getEmail());
        System.out.println("Phone Number   : " + getPhone());
        System.out.println("----------------------------------------------");
    }

    @Override
    public String toString() {
        return getName() + " (" + getId() + " - " + getEmail() + ")";
    }
}