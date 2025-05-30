package com.api.entity;

import com.api.utils.RewardType;
import com.api.utils.RewardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rewards")
public class Reward extends BaseEntity {

    @Column(nullable = false)
    private String title; // Tên phần thưởng

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type; // DAILY, WEEKLY, MONTHLY, ACHIEVEMENT

    @Column(name = "reward_value", precision = 10, scale = 2)
    private BigDecimal rewardValue; // Giá trị phần thưởng (tiền hoặc gems)

    @Column(name = "gems_value")
    private Integer gemsValue; // Số gems nhận được

    @Column(name = "required_orders")
    private Integer requiredOrders; // Số đơn hàng cần hoàn thành

    @Column(name = "required_distance")
    private Float requiredDistance; // Quãng đường cần đi (km)

    @Column(name = "required_rating")
    private Float requiredRating; // Rating tối thiểu cần đạt

    @Column(name = "start_date")
    private LocalDateTime startDate; // Ngày bắt đầu

    @Column(name = "end_date")
    private LocalDateTime endDate; // Ngày kết thúc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus status; // ACTIVE, INACTIVE, EXPIRED

    @Column(name = "icon_url")
    private String iconUrl; // URL icon phần thưởng

    @Column(name = "created_by")
    private String createdBy; // Admin tạo phần thưởng
}