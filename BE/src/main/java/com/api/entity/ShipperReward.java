package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shipper_rewards",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_shipper_reward", columnNames = {"shipper_id", "reward_id"})
       })
public class ShipperReward extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardClaimStatus status = RewardClaimStatus.ELIGIBLE;
    
    @Column(name = "progress_value")
    private Float progressValue;
    
    @Column(name = "completion_percentage")
    private Float completionPercentage;
    
    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;
    
    @Column
    private String notes;
    
    // Enum cho trạng thái claim reward
    public enum RewardClaimStatus {
        ELIGIBLE,  // Đủ điều kiện claim
        CLAIMED,   // Đã claim
        EXPIRED    // Hết hạn
    }
    
    // Phương thức tiện ích
    public void claimReward() {
        this.status = RewardClaimStatus.CLAIMED;
        this.claimedAt = LocalDateTime.now();
        this.completionPercentage = 100.0f;
    }
    
    public void expireReward() {
        this.status = RewardClaimStatus.EXPIRED;
    }
    
    public Boolean isClaimed() {
        return this.status == RewardClaimStatus.CLAIMED;
    }
    
    public Boolean isEligible() {
        return this.status == RewardClaimStatus.ELIGIBLE;
    }
    
    public Boolean isExpired() {
        return this.status == RewardClaimStatus.EXPIRED;
    }
    
    public Boolean canBeClaimed() {
        return isEligible() && completionPercentage != null && completionPercentage >= 100.0f;
    }
    
    public Long getShipperId() {
        return shipper != null ? shipper.getId() : null;
    }
    
    public Long getRewardId() {
        return reward != null ? reward.getId() : null;
    }
    
    // Cập nhật tiến độ
    public void updateProgress(Float currentValue, Float targetValue) {
        this.progressValue = currentValue;
        if (targetValue != null && targetValue > 0) {
            this.completionPercentage = Math.min((currentValue / targetValue) * 100, 100.0f);
        }
    }
    
    public void updateProgress(Integer currentOrders, Integer requiredOrders) {
        if (requiredOrders != null && requiredOrders > 0) {
            updateProgress((float) currentOrders, (float) requiredOrders);
        }
    }
    
    // Kiểm tra xem có đủ điều kiện claim không
    public Boolean checkEligibility() {
        if (reward == null || shipper == null) {
            return false;
        }
        
        // Kiểm tra reward còn hợp lệ
        if (!reward.isValidNow()) {
            expireReward();
            return false;
        }
        
        // Kiểm tra điều kiện của reward
        return reward.canBeClaimed(shipper);
    }
    
    // Tính toán tiến độ dựa trên loại reward
    public void calculateProgress() {
        if (reward == null || shipper == null) {
            return;
        }
        
        switch (reward.getType()) {
            case DAILY:
                if (reward.getRequiredOrders() != null) {
                    updateProgress(shipper.getCompletedOrders(), reward.getRequiredOrders());
                }
                break;
                
            case PEAK_HOUR:
                if (reward.getRequiredDeliveries() != null) {
                    updateProgress(shipper.getCompletedOrders(), reward.getRequiredDeliveries());
                }
                break;
                
            case ACHIEVEMENT:
                if (reward.getRequiredOrders() != null) {
                    updateProgress(shipper.getTotalOrders(), reward.getRequiredOrders());
                } else if (reward.getRequiredRating() != null) {
                    float currentRating = shipper.getRating().floatValue();
                    updateProgress(currentRating, reward.getRequiredRating());
                }
                break;
                
            case BONUS:
                if (reward.getRequiredDeliveries() != null) {
                    updateProgress(shipper.getCompletedOrders(), reward.getRequiredDeliveries());
                }
                break;
        }
    }
    
    // Static factory method
    public static ShipperReward createNewReward(Shipper shipper, Reward reward) {
        ShipperReward shipperReward = ShipperReward.builder()
                .shipper(shipper)
                .reward(reward)
                .status(RewardClaimStatus.ELIGIBLE)
                .build();
                
        shipperReward.calculateProgress();
        return shipperReward;
    }
    
    // Lấy thông tin hiển thị
    public String getProgressDisplay() {
        if (completionPercentage == null) {
            return "0%";
        }
        return String.format("%.1f%%", completionPercentage);
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case ELIGIBLE -> "Đủ điều kiện";
            case CLAIMED -> "Đã nhận";
            case EXPIRED -> "Hết hạn";
        };
    }
} 