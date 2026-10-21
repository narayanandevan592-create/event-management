package com.eventmgmt.service;

import com.eventmgmt.dao.AdminDAO;
import com.eventmgmt.dao.ParticipantDAO;
import com.eventmgmt.exception.ValidationException;
import com.eventmgmt.model.Admin;
import com.eventmgmt.model.Participant;


public class AuthService{
    private final AdminDAO adminDAO;
    private final ParticipantDAO participantDAO;

    // Constructor Injection
    public AuthService(AdminDAO adminDAO, ParticipantDAO participantDAO){
        this.adminDAO = adminDAO;
        this.participantDAO = participantDAO;
    }
    public Admin loginAdmin(String username, String password){
        if (username == null || username.trim().isEmpty()){
            throw new ValidationException("Please enter the Admin Username.");
        }
        if (password == null || password.trim().isEmpty()){
            throw new ValidationException("Please enter the Admin Password.");
        }
        Admin admin = adminDAO.authenticate(username.trim(), password.trim());
        if (admin == null) {
            throw new ValidationException("Invalid administrator credentials. Default credentials: admin / admin123");
        }
        return admin;
    }

    public Participant loginStudent(String emailOrId, String password){
        if (emailOrId == null || emailOrId.trim().isEmpty()){
            throw new ValidationException("Please enter your Student Email or ID.");
        }
        if (password == null || password.trim().isEmpty()){
            throw new ValidationException("Please enter your Password.");
        }

        Participant student = participantDAO.authenticate(emailOrId.trim(), password.trim());
        if (student == null){
            throw new ValidationException("Invalid student credentials. Please check your Email/ID and Password, or Sign Up if new.");
        }
        return student;
    }
    public Participant signUpStudent(String studentId, String name, String email, String phone, String password, String confirmPassword) {
        if (studentId == null || studentId.trim().isEmpty()){
            throw new ValidationException("Student ID is required.");
        }
        if (name == null || name.trim().isEmpty()){
            throw new ValidationException("Full Name is required.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")){
            throw new ValidationException("A valid Email address is required.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new ValidationException("Password must be at least 4 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Password and Confirm Password do not match.");
        }
        Participant existingByEmail = participantDAO.findByEmail(email.trim());
        if (existingByEmail != null) {
            throw new ValidationException("An account with email '" + email.trim() + "' already exists. Please log in.");
        }
        Participant existingById = participantDAO.findById(studentId.trim());
        if (existingById != null) {
            throw new ValidationException("An account with Student ID '" + studentId.trim() + "' already exists.");
        }
        String formattedPhone = (phone != null) ? phone.trim() : "";
        Participant newStudent = new Participant(
            studentId.trim(),
            name.trim(),
            email.trim(),
            formattedPhone,
            password.trim()
        );
        participantDAO.save(newStudent);
        return newStudent;
    }
}