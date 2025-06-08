package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class ShipperProfileUpdate {
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("vehicleType")
    private String vehicleType;
    
    @SerializedName("licensePlate")
    private String licensePlate;

    // Constructor
    public ShipperProfileUpdate(String name, String email, String vehicleType, String licensePlate) {
        this.name = name;
        this.email = email;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
} 