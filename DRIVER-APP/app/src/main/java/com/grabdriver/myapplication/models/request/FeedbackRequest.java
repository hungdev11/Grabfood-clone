package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class FeedbackRequest {
    @SerializedName("subject")
    private String subject;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("priority")
    private String priority;

    // Constructor
    public FeedbackRequest(String subject, String message, String category, String priority) {
        this.subject = subject;
        this.message = message;
        this.category = category;
        this.priority = priority;
    }

    // Getters and Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
} 