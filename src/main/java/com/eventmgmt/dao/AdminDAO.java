package com.eventmgmt.dao;
import com.eventmgmt.model.Admin;
public interface AdminDAO {  
    Admin authenticate(String username, String password);
    Admin findByUsername(String username);
    void save(Admin admin);
}