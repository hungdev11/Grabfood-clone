package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // ==== TODAY'S EARNINGS ====
    private BigDecimal todayEarnings; // Thu nhập hôm nay
    private BigDecimal weeklyEarnings; // Thu nhập tuần này
    private BigDecimal monthlyEarnings; // Thu nhập tháng này

    // ==== ORDER STATISTICS ====
    private Integer todayOrders; // Số đơn hôm nay
    private Integer completedOrders; // Đơn hoàn thành hôm nay
    private Integer cancelledOrders; // Đơn hủy hôm nay
    private Integer pendingOrders; // Đơn đang chờ

    // ==== PERFORMANCE METRICS ====
    private Float averageRating; // Rating trung bình
    private Float completionRate; // Tỷ lệ hoàn thành (%)
    private Float acceptanceRate; // Tỷ lệ nhận đơn (%)

    // ==== ACTIVITY STATS ====
    private Float totalDistanceToday; // Tổng km đi hôm nay
    private Integer totalDeliveryTime; // Tổng thời gian giao hàng (phút)
    private Integer activeHours; // Số giờ online hôm nay

    // ==== BONUS & INCENTIVES ====
    private BigDecimal bonusEarnings; // Tiền thưởng
    private Integer gemsEarned; // Gems tích lũy
    private Long totalTips; // Tips từ khách hàng

    // ==== STATUS INFO ====
    private Boolean isOnline; // Trạng thái online
    private LocalDateTime lastActiveTime; // Lần cuối hoạt động
    private String currentLocation; // Vị trí hiện tại (text)

    // ==== QUICK INSIGHTS ====
    private String bestPerformanceTime; // Khung giờ hiệu suất cao nhất
    private BigDecimal avgOrderValue; // Giá trị đơn hàng trung bình
    private Integer nearbyOrdersCount; // Số đơn gần vị trí hiện tại
}