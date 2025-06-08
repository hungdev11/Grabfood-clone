package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Response DTO cho thông tin phần thưởng của shipper
 * Match với RewardResponse từ backend
 */
public class RewardResponse {
    
    // ===============================
    // THÔNG TIN PHẦN THƯỞNG
    // ===============================
    
    @SerializedName("rewardId")
    private Long rewardId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("iconUrl")
    private String iconUrl;
    
    // ===============================
    // GIÁ TRỊ & ĐIỀU KIỆN
    // ===============================
    
    @SerializedName("type")
    private String type; // DAILY, PEAK_HOUR, BONUS, ACHIEVEMENT
    
    @SerializedName("rewardValue")
    private BigDecimal rewardValue;
    
    @SerializedName("gemsValue")
    private Integer gemsValue;
    
    @SerializedName("status")
    private String status; // ACTIVE, EXPIRED, INACTIVE
    
    // ===============================
    // ĐIỀU KIỆN HOÀN THÀNH
    // ===============================
    
    @SerializedName("requiredOrders")
    private Integer requiredOrders;
    
    @SerializedName("requiredDeliveries")
    private Integer requiredDeliveries;
    
    @SerializedName("requiredDistance")
    private Float requiredDistance;
    
    @SerializedName("requiredRating")
    private Float requiredRating;
    
    // ===============================
    // THỜI GIAN
    // ===============================
    
    @SerializedName("startDate")
    private Date startDate;
    
    @SerializedName("endDate")
    private Date endDate;
    
    @SerializedName("peakStartTime")
    private String peakStartTime;
    
    @SerializedName("peakEndTime")
    private String peakEndTime;
    
    // ===============================
    // TIẾN ĐỘ SHIPPER
    // ===============================
    
    @SerializedName("shipperStatus")
    private String shipperStatus; // ELIGIBLE, CLAIMED, EXPIRED
    
    @SerializedName("progressValue")
    private Float progressValue;
    
    @SerializedName("completionPercentage")
    private Float completionPercentage;
    
    @SerializedName("claimedAt")
    private Date claimedAt;
    
    @SerializedName("progressNotes")
    private String progressNotes;
    
    // ===============================
    // THÔNG TIN Bổ SUNG
    // ===============================
    
    @SerializedName("isActive")
    private Boolean isActive;
    
    @SerializedName("canClaim")
    private Boolean canClaim;
    
    @SerializedName("daysLeft")
    private Integer daysLeft;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("priority")
    private Integer priority;

    // Constructors
    public RewardResponse() {
    }

    // Convert to Reward for compatibility with existing adapter
    public Reward toReward() {
        Reward reward = new Reward();
        reward.setId(rewardId != null ? rewardId : 0);
        reward.setTitle(title);
        reward.setName(name);
        reward.setDescription(description);
        reward.setIconUrl(iconUrl);
        reward.setType(type);
        reward.setRewardValue(rewardValue);
        reward.setGemsValue(gemsValue);
        reward.setStatus(shipperStatus != null ? shipperStatus : status);
        reward.setRequiredOrders(requiredOrders);
        reward.setRequiredDeliveries(requiredDeliveries);
        reward.setRequiredDistance(requiredDistance);
        reward.setRequiredRating(requiredRating);
        reward.setStartDate(startDate);
        reward.setEndDate(endDate);
        reward.setPeakStartTime(peakStartTime);
        reward.setPeakEndTime(peakEndTime);
        reward.setActive(isActive != null ? isActive : false);
        return reward;
    }

    // Getters and Setters
    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(BigDecimal rewardValue) {
        this.rewardValue = rewardValue;
    }

    public Integer getGemsValue() {
        return gemsValue;
    }

    public void setGemsValue(Integer gemsValue) {
        this.gemsValue = gemsValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRequiredOrders() {
        return requiredOrders;
    }

    public void setRequiredOrders(Integer requiredOrders) {
        this.requiredOrders = requiredOrders;
    }

    public Integer getRequiredDeliveries() {
        return requiredDeliveries;
    }

    public void setRequiredDeliveries(Integer requiredDeliveries) {
        this.requiredDeliveries = requiredDeliveries;
    }

    public Float getRequiredDistance() {
        return requiredDistance;
    }

    public void setRequiredDistance(Float requiredDistance) {
        this.requiredDistance = requiredDistance;
    }

    public Float getRequiredRating() {
        return requiredRating;
    }

    public void setRequiredRating(Float requiredRating) {
        this.requiredRating = requiredRating;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPeakStartTime() {
        return peakStartTime;
    }

    public void setPeakStartTime(String peakStartTime) {
        this.peakStartTime = peakStartTime;
    }

    public String getPeakEndTime() {
        return peakEndTime;
    }

    public void setPeakEndTime(String peakEndTime) {
        this.peakEndTime = peakEndTime;
    }

    public String getShipperStatus() {
        return shipperStatus;
    }

    public void setShipperStatus(String shipperStatus) {
        this.shipperStatus = shipperStatus;
    }

    public Float getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(Float progressValue) {
        this.progressValue = progressValue;
    }

    public Float getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Float completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Date getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Date claimedAt) {
        this.claimedAt = claimedAt;
    }

    public String getProgressNotes() {
        return progressNotes;
    }

    public void setProgressNotes(String progressNotes) {
        this.progressNotes = progressNotes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getCanClaim() {
        return canClaim;
    }

    public void setCanClaim(Boolean canClaim) {
        this.canClaim = canClaim;
    }

    public Integer getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
} 