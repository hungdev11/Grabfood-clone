package com.api.entity;

import com.api.utils.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipper_rewards")
public class ShipperReward extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt; // Thời điểm nhận thưởng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status; // ELIGIBLE, CLAIMED, EXPIRED

    @Column(name = "progress_value")
    private Float progressValue; // Tiến độ hiện tại (số đơn, km, rating...)

    @Column(name = "completion_percentage")
    private Float completionPercentage; // Phần trăm hoàn thành

    @Column(name = "notes")
    private String notes; // Ghi chú thêm
}