package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO cho thống kê profile của driver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatsResponse {
    
    // Thống kê cơ bản
    private Integer totalOrders;           // Tổng số đơn hàng
    private Integer completedOrders;       // Số đơn hoàn thành
    private Integer cancelledOrders;       // Số đơn hủy
    private Double acceptanceRate;         // Tỷ lệ chấp nhận (%)
    private Double completionRate;         // Tỷ lệ hoàn thành (%)
    private Double rating;                 // Đánh giá trung bình
    private Integer totalReviews;          // Tổng số đánh giá
    
    // Thống kê thời gian
    private Integer onlineHoursToday;      // Số giờ online hôm nay
    private Integer onlineHoursThisWeek;   // Số giờ online tuần này
    private Integer onlineHoursThisMonth;  // Số giờ online tháng này
    
    // Thống kê thu nhập
    private BigDecimal totalEarnings;      // Tổng thu nhập
    private BigDecimal todayEarnings;      // Thu nhập hôm nay
    private BigDecimal weekEarnings;       // Thu nhập tuần này
    private BigDecimal monthEarnings;      // Thu nhập tháng này
    private BigDecimal averageEarningPerOrder; // Thu nhập trung bình/đơn
    
    // Thống kê rewards
    private Integer totalGems;             // Tổng số gems
    private Integer gemsEarnedThisMonth;   // Gems kiếm được tháng này
    private Integer totalRewardsClaimed;   // Tổng số reward đã claim
    private Integer availableRewards;      // Số reward có thể claim
    
    // Khoảng cách và thời gian
    private Double totalDistanceKm;        // Tổng khoảng cách đã đi (km)
    private Integer averageDeliveryTimeMinutes; // Thời gian giao hàng trung bình
    
    // Thông tin rank và thành tích
    private String currentRank;            // Rank hiện tại
    private Integer rankPosition;          // Vị trí trong rank
    private String[] achievements;         // Danh sách thành tích
    
    // Thời gian cập nhật
    private LocalDateTime lastUpdated;     // Lần cập nhật cuối
    private LocalDateTime memberSince;     // Ngày gia nhập
} 