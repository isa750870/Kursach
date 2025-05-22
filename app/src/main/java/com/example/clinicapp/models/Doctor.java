package com.example.clinicapp.models;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String photoUrl;

    public Doctor(int id, String name, String specialization, String photoUrl) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.photoUrl = photoUrl;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getPhotoUrl() { return photoUrl; }

    @Override
    public String toString() {
        return name + " - " + specialization;
    }
}