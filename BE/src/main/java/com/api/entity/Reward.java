package com.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rewards")
public class Reward extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column
    private RewardType type;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "reward_value", precision = 10, scale = 2)
    private BigDecimal rewardValue;
    
    @Column(name = "gems_value")
    private Integer gemsValue;
    
    @Column(name = "required_orders")
    private Integer requiredOrders;
    
    @Column(name = "required_deliveries")
    private Integer requiredDeliveries;
    
    @Column(name = "required_distance")
    private Float requiredDistance;
    
    @Column(name = "required_rating")
    private Float requiredRating;
    
    @Column(name = "peak_start_time")
    private LocalTime peakStartTime;
    
    @Column(name = "peak_end_time")
    private LocalTime peakEndTime;
    
    @Column(name = "valid_from")
    private LocalDate validFrom;
    
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus status = RewardStatus.ACTIVE;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "created_by")
    private String createdBy;
    
    // Quan hệ với ShipperReward
    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipperReward> shipperRewards = new ArrayList<>();
    
    // Enum cho loại reward
    public enum RewardType {
        DAILY,       // Thưởng hàng ngày
        PEAK_HOUR,   // Thưởng giờ cao điểm  
        BONUS,       // Thưởng đặc biệt
        ACHIEVEMENT  // Thành tựu
    }
    
    // Enum cho trạng thái reward
    public enum RewardStatus {
        ACTIVE,    // Đang hoạt động
        INACTIVE,  // Ngừng hoạt động
        EXPIRED    // Hết hạn
    }
    
    // Phương thức kiểm tra tính hợp lệ
    public Boolean isValidNow() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        
        // Kiểm tra trạng thái
        if (!isActive || status != RewardStatus.ACTIVE) {
            return false;
        }
        
        // Kiểm tra thời gian hợp lệ
        if (validFrom != null && today.isBefore(validFrom)) {
            return false;
        }
        
        if (validTo != null && today.isAfter(validTo)) {
            return false;
        }
        
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
    
    public Boolean isPeakHourReward() {
        return type == RewardType.PEAK_HOUR && peakStartTime != null && peakEndTime != null;
    }
    
    public Boolean isInPeakHour() {
        if (!isPeakHourReward()) {
            return false;
        }
        
        LocalTime now = LocalTime.now();
        return !now.isBefore(peakStartTime) && !now.isAfter(peakEndTime);
    }
    
    public Boolean isDailyReward() {
        return type == RewardType.DAILY;
    }
    
    public Boolean isAchievementReward() {
        return type == RewardType.ACHIEVEMENT;
    }
    
    public Boolean isBonusReward() {
        return type == RewardType.BONUS;
    }
    
    // Kiểm tra điều kiện để claim reward
    public Boolean canBeClaimed(Shipper shipper) {
        if (!isValidNow()) {
            return false;
        }
        
        // Kiểm tra số đơn hàng yêu cầu
        if (requiredOrders != null && shipper.getCompletedOrders() < requiredOrders) {
            return false;
        }
        
        // Kiểm tra rating yêu cầu
        if (requiredRating != null && shipper.getRating().floatValue() < requiredRating) {
            return false;
        }
        
        // Kiểm tra deliveries yêu cầu
        if (requiredDeliveries != null && shipper.getCompletedOrders() < requiredDeliveries) {
            return false;
        }
        
        return true;
    }
    
    public void expire() {
        this.status = RewardStatus.EXPIRED;
        this.isActive = false;
    }
    
    public void activate() {
        this.status = RewardStatus.ACTIVE;
        this.isActive = true;
    }
    
    public void deactivate() {
        this.status = RewardStatus.INACTIVE;
        this.isActive = false;
    }
    
    // Phương thức tính toán giá trị reward
    public BigDecimal getCalculatedValue() {
        return rewardValue != null ? rewardValue : amount;
    }
    
    public Integer getCalculatedGems() {
        return gemsValue != null ? gemsValue : 0;
    }
    
    // Phương thức tiện ích cho display
    public String getRewardTypeDisplay() {
        return switch (type) {
            case DAILY -> "Thưởng hàng ngày";
            case PEAK_HOUR -> "Thưởng giờ cao điểm";
            case BONUS -> "Thưởng đặc biệt";
            case ACHIEVEMENT -> "Thành tựu";
        };
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case ACTIVE -> "Đang hoạt động";
            case INACTIVE -> "Ngừng hoạt động";
            case EXPIRED -> "Hết hạn";
        };
    }
} 