package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class RejectOrderRequest {
    @SerializedName("rejectionReason")
    private String rejectionReason;
    
    @SerializedName("note")
    private String note;

    public RejectOrderRequest(String rejectionReason, String note) {
        this.rejectionReason = rejectionReason;
        this.note = note;
    }

    // Getters and Setters
    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 