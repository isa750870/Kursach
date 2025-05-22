package com.example.clinicapp.models;

public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String phone;

    public User(int id, String email, String password, String fullName, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
}