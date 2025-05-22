package com.example.clinicapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private int id;
    private int userId;
    private int doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Date dateTime;

    public Appointment(int id, int userId, int doctorId, String doctorName, String doctorSpecialization, Date dateTime) {
        this.id = id;
        this.userId = userId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.dateTime = dateTime;
    }

    // Геттеры
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDoctorSpecialization() { return doctorSpecialization; }
    public Date getDateTime() { return dateTime; }

    public String getFormattedDate() {
        return new SimpleDateFormat("dd MMMM", new Locale("ru")).format(dateTime);
    }

    public String getFormattedTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime);
    }

    public String getFormattedDay() {
        return new SimpleDateFormat("EEEE", new Locale("ru")).format(dateTime);
    }

    public String getFormattedDateTimeForDialog() {
        return new SimpleDateFormat("EEEE, d MMMM yyyy 'в' HH:mm", new Locale("ru")).format(dateTime);
    }
}