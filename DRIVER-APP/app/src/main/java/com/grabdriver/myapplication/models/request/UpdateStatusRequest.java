package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRequest {
    @SerializedName("note")
    private String note;

    public UpdateStatusRequest(String note) {
        this.note = note;
    }

    // Getters and Setters
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 