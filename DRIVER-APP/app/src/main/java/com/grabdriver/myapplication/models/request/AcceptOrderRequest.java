package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class AcceptOrderRequest {
    @SerializedName("estimatedPickupTime")
    private String estimatedPickupTime;
    
    @SerializedName("estimatedDeliveryTime")
    private String estimatedDeliveryTime;
    
    @SerializedName("note")
    private String note;

    public AcceptOrderRequest(String estimatedPickupTime, String estimatedDeliveryTime, String note) {
        this.estimatedPickupTime = estimatedPickupTime;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.note = note;
    }

    // Getters and Setters
    public String getEstimatedPickupTime() {
        return estimatedPickupTime;
    }

    public void setEstimatedPickupTime(String estimatedPickupTime) {
        this.estimatedPickupTime = estimatedPickupTime;
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 