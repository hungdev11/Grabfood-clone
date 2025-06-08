package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;
    
    @SerializedName("shipperId")
    private Long shipperId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("rating")
    private double rating;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("isOnline")
    private boolean isOnline;
    
    @SerializedName("vehicleType")
    private String vehicleType;
    
    @SerializedName("licensePlate")
    private String licensePlate;
    
    @SerializedName("totalOrders")
    private int totalOrders;
    
    @SerializedName("completedOrders")
    private int completedOrders;
    
    @SerializedName("acceptanceRate")
    private float acceptanceRate;
    
    @SerializedName("gems")
    private int gems;
    
    @SerializedName("currentLatitude")
    private Double currentLatitude;
    
    @SerializedName("currentLongitude")
    private Double currentLongitude;
    
    @SerializedName("message")
    private String message;
    
    private boolean success;
    private Shipper shipperInfo;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long shipperId, Shipper shipperInfo) {
        this.token = token;
        this.shipperId = shipperId;
        this.shipperInfo = shipperInfo;
        this.success = true;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public Shipper getShipperInfo() {
        if (shipperInfo == null) {
            // Create Shipper object from response data
            shipperInfo = new Shipper();
            shipperInfo.setId(shipperId);
            shipperInfo.setName(name);
            shipperInfo.setPhone(phone);
            shipperInfo.setEmail(email);
            shipperInfo.setRating(rating);
            shipperInfo.setStatus(status);
            shipperInfo.setOnline(isOnline);
            shipperInfo.setVehicleType(vehicleType);
            shipperInfo.setLicensePlate(licensePlate);
            shipperInfo.setTotalOrders(totalOrders);
            shipperInfo.setCompletedOrders(completedOrders);
            shipperInfo.setAcceptanceRate(acceptanceRate);
            shipperInfo.setGems(gems);
            shipperInfo.setCurrentLatitude(currentLatitude);
            shipperInfo.setCurrentLongitude(currentLongitude);
        }
        return shipperInfo;
    }

    public void setShipperInfo(Shipper shipperInfo) {
        this.shipperInfo = shipperInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Additional getters for new fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
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

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(int completedOrders) {
        this.completedOrders = completedOrders;
    }

    public float getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(float acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "shipperId=" + shipperId +
                ", name='" + name + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", hasToken=" + (token != null && !token.isEmpty()) +
                '}';
    }
}