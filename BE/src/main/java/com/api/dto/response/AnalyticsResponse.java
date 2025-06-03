package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO cho thống kê analytics của shipper
 * Dùng cho hệ thống phân tích trong Phase 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    
    // ===============================
    // THỐNG KÊ HIỆU SUẤT
    // ===============================
    
    private Long shipperId;                    // ID shipper
    private String shipperName;                // Tên shipper
    private String period;                     // Kỳ thống kê (today, week, month, year)
    
    // ===============================
    // THỐNG KÊ ĐỐN HÀNG  
    // ===============================
    
    private Integer totalOrders;               // Tổng số đơn hàng
    private Integer completedOrders;           // Đơn hàng hoàn thành
    private Integer cancelledOrders;           // Đơn hàng hủy
    private Integer rejectedOrders;            // Đơn hàng từ chối
    private Double completionRate;             // Tỷ lệ hoàn thành (%)
    private Double acceptanceRate;             // Tỷ lệ chấp nhận (%)
    
    // ===============================
    // THỐNG KÊ THỜI GIAN
    // ===============================
    
    private Integer totalWorkingHours;         // Tổng giờ làm việc
    private Integer onlineHours;               // Giờ online
    private Integer averageDeliveryTime;       // Thời gian giao hàng trung bình (phút)
    private Integer fastestDelivery;           // Giao hàng nhanh nhất (phút)
    private Integer slowestDelivery;           // Giao hàng chậm nhất (phút)
    
    // ===============================
    // THỐNG KÊ KHOẢNG CÁCH
    // ===============================
    
    private Double totalDistance;              // Tổng khoảng cách (km)
    private Double averageDistance;            // Khoảng cách trung bình/đơn (km)
    private Double longestTrip;                // Chuyến đi xa nhất (km)
    private Double shortestTrip;               // Chuyến đi gần nhất (km)
    
    // ===============================
    // THỐNG KÊ THU NHẬP
    // ===============================
    
    private BigDecimal totalEarnings;          // Tổng thu nhập
    private BigDecimal deliveryFees;           // Phí giao hàng
    private BigDecimal tips;                   // Tiền tip
    private BigDecimal bonuses;                // Tiền thưởng
    private BigDecimal averageEarningPerOrder; // Thu nhập trung bình/đơn
    private BigDecimal averageEarningPerHour;  // Thu nhập trung bình/giờ
    
    // ===============================
    // THỐNG KÊ ĐÁNH GIÁ
    // ===============================
    
    private Double currentRating;              // Đánh giá hiện tại
    private Integer totalReviews;              // Tổng số đánh giá
    private Integer fiveStarCount;             // Số đánh giá 5 sao
    private Integer fourStarCount;             // Số đánh giá 4 sao
    private Integer threeStarCount;            // Số đánh giá 3 sao
    private Integer twoStarCount;              // Số đánh giá 2 sao
    private Integer oneStarCount;              // Số đánh giá 1 sao
    
    // ===============================
    // THỐNG KÊ THEO THỜI GIAN
    // ===============================
    
    private Map<String, Integer> hourlyOrders; // Đơn hàng theo giờ trong ngày
    private Map<String, Integer> dailyOrders;  // Đơn hàng theo ngày trong tuần
    private Map<String, BigDecimal> dailyEarnings; // Thu nhập theo ngày
    private List<PeakHourData> peakHours;      // Dữ liệu giờ cao điểm
    
    // ===============================
    // THỐNG KÊ KHUYEN SÁCH
    // ===============================
    
    private Integer totalGems;                 // Tổng gems
    private Integer totalRewards;              // Tổng phần thưởng
    private String currentRank;                // Rank hiện tại
    private Integer rankPosition;              // Vị trí rank
    private String[] achievements;             // Thành tích đạt được
    
    // ===============================
    // THÔNG TIN THỜI GIAN
    // ===============================
    
    private LocalDateTime reportGeneratedAt;   // Thời gian tạo báo cáo
    private LocalDateTime periodStart;         // Bắt đầu kỳ thống kê
    private LocalDateTime periodEnd;           // Kết thúc kỳ thống kê
    private LocalDateTime lastUpdated;         // Cập nhật cuối cùng
    
    // ===============================
    // NESTED CLASSES
    // ===============================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeakHourData {
        private String timeSlot;               // Khung giờ (VD: "11:00-13:00")
        private Integer orderCount;            // Số đơn hàng
        private BigDecimal earnings;           // Thu nhập trong khung giờ
        private Double averageDeliveryTime;    // Thời gian giao hàng trung bình
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String date;                   // Ngày
        private Integer orders;                // Số đơn hàng
        private BigDecimal earnings;           // Thu nhập
        private Double rating;                 // Đánh giá trung bình trong ngày
    }
} 