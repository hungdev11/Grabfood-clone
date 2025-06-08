package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("status")
    private boolean status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;
    
    @SerializedName("error")
    private String error;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("code")
    private int code;
    
    // Getters and Setters
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
} 