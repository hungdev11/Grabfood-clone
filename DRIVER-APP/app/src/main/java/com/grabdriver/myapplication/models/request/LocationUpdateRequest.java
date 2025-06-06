package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class LocationUpdateRequest {
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    @SerializedName("isOnline")
    private boolean isOnline;

    public LocationUpdateRequest(double latitude, double longitude, boolean isOnline) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
} 