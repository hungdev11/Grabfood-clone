package com.grabdriver.fe.models;

import java.util.Date;

public class Reward {
    private String id;
    private Long shipperId;
    private String type; // daily, weekly
    private String title;
    private String description;
    private int gemsRequired;
    private long cashReward;
    private int currentProgress;
    private int targetProgress;
    private boolean isCompleted;
    private Date expiryDate;
    private Date completedDate;
    private Date dateEarned;
    private Date datePaid;
    private String status; // PENDING, PAID
    private int totalGems;
    private double acceptanceRate;
    private double cancellationRate;
    private double averageRating;

    // Constructors
    public Reward() {}

    public Reward(String type, int gemsRequired, long cashReward) {
        this.type = type;
        this.gemsRequired = gemsRequired;
        this.cashReward = cashReward;
        this.dateEarned = new Date();
        this.status = "PENDING";
        this.isCompleted = false;
        this.currentProgress = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getShipperId() { return shipperId; }
    public void setShipperId(Long shipperId) { this.shipperId = shipperId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getGemsRequired() { return gemsRequired; }
    public void setGemsRequired(int gemsRequired) { this.gemsRequired = gemsRequired; }

    public long getCashReward() { return cashReward; }
    public void setCashReward(long cashReward) { this.cashReward = cashReward; }

    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }

    public int getTargetProgress() { return targetProgress; }
    public void setTargetProgress(int targetProgress) { this.targetProgress = targetProgress; }

    public boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }

    public Date getDateEarned() { return dateEarned; }
    public void setDateEarned(Date dateEarned) { this.dateEarned = dateEarned; }

    public Date getDatePaid() { return datePaid; }
    public void setDatePaid(Date datePaid) { this.datePaid = datePaid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalGems() { return totalGems; }
    public void setTotalGems(int totalGems) { this.totalGems = totalGems; }

    public double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(double acceptanceRate) { this.acceptanceRate = acceptanceRate; }

    public double getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(double cancellationRate) { this.cancellationRate = cancellationRate; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    // Keep old method for compatibility
    public double getRewardAmount() { return cashReward; }
    public void setRewardAmount(double rewardAmount) { this.cashReward = (long) rewardAmount; }

    // Helper methods
    public String getFormattedRewardAmount() {
        return String.format("%,.0f đ", (double) cashReward);
    }

    public boolean isEligible() {
        return acceptanceRate >= 90.0 && cancellationRate <= 10.0 && averageRating >= 4.8;
    }

    public String getRewardDescription() {
        if ("daily".equals(type)) {
            if (gemsRequired == 700) {
                return "Thưởng ngày - 700 ngọc";
            } else if (gemsRequired == 400) {
                return "Thưởng ngày - 400 ngọc";
            }
        } else if ("weekly".equals(type)) {
            if (gemsRequired == 2500) {
                return "Thưởng tuần - 2500 ngọc";
            } else if (gemsRequired == 1400) {
                return "Thưởng tuần - 1400 ngọc";
            }
        }
        return "Thưởng " + type.toLowerCase();
    }
} 