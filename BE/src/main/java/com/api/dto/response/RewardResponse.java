package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO cho thông tin phần thưởng của shipper
 * Dùng cho hệ thống rewards trong Phase 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    
    // ===============================
    // THÔNG TIN PHẦN THƯỞNG
    // ===============================
    
    private Long rewardId;                     // ID phần thưởng
    private String title;                      // Tiêu đề phần thưởng
    private String name;                       // Tên phần thưởng
    private String description;                // Mô tả chi tiết
    private String iconUrl;                    // URL icon phần thưởng
    
    // ===============================
    // GIÁ TRỊ & ĐIỀU KIỆN
    // ===============================
    
    private String type;                       // Loại phần thưởng (DAILY, PEAK_HOUR, BONUS, ACHIEVEMENT)
    private BigDecimal rewardValue;            // Giá trị phần thưởng (VND)
    private Integer gemsValue;                 // Số gems nhận được
    private String status;                     // Trạng thái (ACTIVE, EXPIRED, INACTIVE)
    
    // ===============================
    // ĐIỀU KIỆN HOÀN THÀNH
    // ===============================
    
    private Integer requiredOrders;            // Số đơn hàng yêu cầu
    private Integer requiredDeliveries;        // Số lần giao hàng yêu cầu
    private Float requiredDistance;            // Khoảng cách yêu cầu (km)
    private Float requiredRating;              // Đánh giá tối thiểu
    
    // ===============================
    // THỜI GIAN
    // ===============================
    
    private LocalDateTime startDate;           // Ngày bắt đầu
    private LocalDateTime endDate;             // Ngày kết thúc
    private String peakStartTime;              // Giờ bắt đầu cao điểm
    private String peakEndTime;                // Giờ kết thúc cao điểm
    
    // ===============================
    // TIẾN ĐỘ SHIPPER
    // ===============================
    
    private String shipperStatus;              // Trạng thái của shipper với reward này (ELIGIBLE, CLAIMED, EXPIRED)
    private Float progressValue;               // Giá trị tiến độ hiện tại
    private Float completionPercentage;        // Phần trăm hoàn thành
    private LocalDateTime claimedAt;           // Thời gian đã nhận thưởng
    private String progressNotes;              // Ghi chú về tiến độ
    
    // ===============================
    // THÔNG TIN Bổ SUNG
    // ===============================
    
    private Boolean isActive;                  // Có đang hoạt động không
    private Boolean canClaim;                  // Có thể nhận thưởng không
    private Integer daysLeft;                  // Số ngày còn lại
    private String category;                   // Danh mục phần thưởng
    private Integer priority;                  // Độ ưu tiên hiển thị
} 