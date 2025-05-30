package com.api.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardProgressResponse {

    // ==== OVERALL PROGRESS ====
    private Integer totalRewards; // Tổng số phần thưởng có sẵn
    private Integer claimedRewards; // Số phần thưởng đã nhận
    private Integer eligibleRewards; // Số phần thưởng có thể nhận

    // ==== TODAY'S PROGRESS ====
    private DailyProgress todayProgress;

    // ==== WEEKLY PROGRESS ====
    private WeeklyProgress weeklyProgress;

    // ==== MONTHLY PROGRESS ====
    private MonthlyProgress monthlyProgress;

    // ==== AVAILABLE REWARDS ====
    private List<RewardResponse> availableRewards;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private Integer ordersCompleted; // Đơn hàng hoàn thành hôm nay
        private Integer ordersTarget; // Mục tiêu đơn hàng hôm nay
        private Float distanceTraveled; // Quãng đường đã đi (km)
        private Float distanceTarget; // Mục tiêu quãng đường
        private Float currentRating; // Rating hiện tại
        private Boolean dailyRewardClaimed; // Đã nhận thưởng ngày chưa
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyProgress {
        private Integer ordersCompleted; // Đơn hàng hoàn thành tuần này
        private Integer ordersTarget; // Mục tiêu đơn hàng tuần
        private Float distanceTraveled; // Quãng đường đã đi tuần này
        private Float distanceTarget; // Mục tiêu quãng đường tuần
        private Boolean weeklyRewardClaimed; // Đã nhận thưởng tuần chưa
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyProgress {
        private Integer ordersCompleted; // Đơn hàng hoàn thành tháng này
        private Integer ordersTarget; // Mục tiêu đơn hàng tháng
        private Float distanceTraveled; // Quãng đường đã đi tháng này
        private Float distanceTarget; // Mục tiêu quãng đường tháng
        private Boolean monthlyRewardClaimed; // Đã nhận thưởng tháng chưa
    }
}