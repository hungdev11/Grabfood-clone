package com.grabdriver.fe.models;

public class Shipper {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String vehicleNumber;
    private String vehicleType;
    private boolean isOnline;
    private double currentLatitude;
    private double currentLongitude;
    private int gems;
    private float rating;
    private int totalOrders;
    private float acceptanceRate;
    private float cancellationRate;
    private long totalEarnings;

    // Constructors
    public Shipper() {}

    public Shipper(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isOnline = false;
        this.gems = 0;
        this.rating = 5.0f;
        this.totalOrders = 0;
        this.acceptanceRate = 100.0f;
        this.cancellationRate = 0.0f;
        this.totalEarnings = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public boolean getIsOnline() { return isOnline; }
    public void setIsOnline(boolean isOnline) { this.isOnline = isOnline; }

    public double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }

    public double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }

    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }

    public int getTotalGems() { return gems; }
    public void setTotalGems(int totalGems) { this.gems = totalGems; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public float getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(float acceptanceRate) { this.acceptanceRate = acceptanceRate; }

    public float getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(float cancellationRate) { this.cancellationRate = cancellationRate; }

    public long getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(long totalEarnings) { this.totalEarnings = totalEarnings; }
} 