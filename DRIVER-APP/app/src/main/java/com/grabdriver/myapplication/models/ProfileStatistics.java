package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class ProfileStatistics {
    @SerializedName("totalDeliveries")
    private int totalDeliveries;
    
    @SerializedName("totalEarnings")
    private double totalEarnings;
    
    @SerializedName("totalDistance")
    private double totalDistance;
    
    @SerializedName("completionRate")
    private double completionRate;
    
    @SerializedName("acceptanceRate")
    private double acceptanceRate;
    
    @SerializedName("averageRating")
    private double averageRating;
    
    @SerializedName("totalHoursOnline")
    private double totalHoursOnline;
    
    @SerializedName("currentStreak")
    private int currentStreak;
    
    @SerializedName("bestStreak")
    private int bestStreak;
    
    @SerializedName("totalCancellations")
    private int totalCancellations;

    // Getters and Setters
    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getTotalHoursOnline() {
        return totalHoursOnline;
    }

    public void setTotalHoursOnline(double totalHoursOnline) {
        this.totalHoursOnline = totalHoursOnline;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getTotalCancellations() {
        return totalCancellations;
    }

    public void setTotalCancellations(int totalCancellations) {
        this.totalCancellations = totalCancellations;
    }

    // Additional methods for compatibility
    public int getTotalOrders() {
        return totalDeliveries;
    }

    public int getCompletedOrders() {
        return (int) (totalDeliveries * completionRate / 100);
    }
} 