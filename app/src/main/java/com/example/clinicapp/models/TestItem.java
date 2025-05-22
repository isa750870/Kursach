package com.example.clinicapp.models;

public class TestItem {
    private int id;
    private String name;
    private String date;

    public TestItem(int id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return name + " (заказан " + date + ")";
    }
}