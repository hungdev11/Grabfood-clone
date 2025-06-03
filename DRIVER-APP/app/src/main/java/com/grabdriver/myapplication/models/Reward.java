package com.grabdriver.myapplication.models;

import java.math.BigDecimal;
import java.util.Date;

public class Reward {
    private long id;
    private BigDecimal amount;
    private String description;
    private boolean isActive;
    private String name;
    private String peakEndTime;
    private String peakStartTime;
    private Integer requiredDeliveries;
    private String type; // DAILY, PEAK_HOUR, BONUS, ACHIEVEMENT
    private Date validFrom;
    private Date validTo;
    private String createdBy;
    private Date endDate;
    private Integer gemsValue;
    private String iconUrl;
    private Float requiredDistance;
    private Integer requiredOrders;
    private Float requiredRating;
    private BigDecimal rewardValue;
    private Date startDate;
    private String status; // ACTIVE, EXPIRED, INACTIVE
    private String title;

    // Constructors
    public Reward() {
    }

    public Reward(long id, String title, String description, String type, BigDecimal rewardValue,
            String status, String iconUrl, Date endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.rewardValue = rewardValue;
        this.status = status;
        this.iconUrl = iconUrl;
        this.endDate = endDate;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeakEndTime() {
        return peakEndTime;
    }

    public void setPeakEndTime(String peakEndTime) {
        this.peakEndTime = peakEndTime;
    }

    public String getPeakStartTime() {
        return peakStartTime;
    }

    public void setPeakStartTime(String peakStartTime) {
        this.peakStartTime = peakStartTime;
    }

    public Integer getRequiredDeliveries() {
        return requiredDeliveries;
    }

    public void setRequiredDeliveries(Integer requiredDeliveries) {
        this.requiredDeliveries = requiredDeliveries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getGemsValue() {
        return gemsValue;
    }

    public void setGemsValue(Integer gemsValue) {
        this.gemsValue = gemsValue;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Float getRequiredDistance() {
        return requiredDistance;
    }

    public void setRequiredDistance(Float requiredDistance) {
        this.requiredDistance = requiredDistance;
    }

    public Integer getRequiredOrders() {
        return requiredOrders;
    }

    public void setRequiredOrders(Integer requiredOrders) {
        this.requiredOrders = requiredOrders;
    }

    public Float getRequiredRating() {
        return requiredRating;
    }

    public void setRequiredRating(Float requiredRating) {
        this.requiredRating = requiredRating;
    }

    public BigDecimal getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(BigDecimal rewardValue) {
        this.rewardValue = rewardValue;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}