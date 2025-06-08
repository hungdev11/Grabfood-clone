package com.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

/**
 * Request DTO cho gửi feedback của shipper
 * Dùng cho hệ thống system utilities trong Phase 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    
    // ===============================
    // THÔNG TIN FEEDBACK
    // ===============================
    
    @NotNull(message = "Loại feedback không được để trống")
    @Size(min = 3, max = 50, message = "Loại feedback phải từ 3-50 ký tự")
    private String type;                       // Loại feedback (BUG, FEATURE_REQUEST, COMPLAINT, SUGGESTION, OTHER)
    
    @NotNull(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 200, message = "Tiêu đề phải từ 5-200 ký tự")
    private String title;                      // Tiêu đề feedback
    
    @NotNull(message = "Nội dung không được để trống")
    @Size(min = 10, max = 2000, message = "Nội dung phải từ 10-2000 ký tự")
    private String message;                    // Nội dung chi tiết
    
    @Size(max = 100, message = "Danh mục không được quá 100 ký tự")
    private String category;                   // Danh mục (APP, PAYMENT, ORDER, SYSTEM, OTHER)
    
    // ===============================
    // MỨC ĐỘ ƯU TIÊN
    // ===============================
    
    private String priority;                   // Mức độ ưu tiên (LOW, MEDIUM, HIGH, URGENT)
    private Integer severity;                  // Mức độ nghiêm trọng (1-5)
    
    // ===============================
    // THÔNG TIN LIÊN HỆ
    // ===============================
    
    @Email(message = "Email không hợp lệ")
    private String contactEmail;               // Email liên hệ (tùy chọn)
    
    private String contactPhone;               // Số điện thoại liên hệ (tùy chọn)
    private Boolean allowContact;              // Cho phép liên hệ lại không
    
    // ===============================
    // THÔNG TIN KỸ THUẬT
    // ===============================
    
    private String deviceInfo;                 // Thông tin thiết bị
    private String appVersion;                 // Phiên bản ứng dụng
    private String osVersion;                  // Phiên bản hệ điều hành
    private String errorCode;                  // Mã lỗi (nếu là bug report)
    private String stackTrace;                // Stack trace (nếu có)
    
    // ===============================
    // FILE ĐÍNH KÈM
    // ===============================
    
    private String[] attachmentUrls;           // URLs của file đính kèm (ảnh, video)
    private String[] logFiles;                 // URLs của log files
    
    // ===============================
    // THÔNG TIN BỔ SUNG
    // ===============================
    
    private String stepsToReproduce;           // Các bước tái hiện lỗi
    private String expectedBehavior;           // Hành vi mong đợi
    private String actualBehavior;             // Hành vi thực tế
    private String additionalInfo;             // Thông tin bổ sung
    
    // ===============================
    // METADATA
    // ===============================
    
    private String sessionId;                  // Session ID khi xảy ra lỗi
    private String userAgent;                  // User agent string
    private String ipAddress;                  // IP address
    private String location;                   // Vị trí khi gửi feedback
    
    // ===============================
    // CÁC CỜ TÙYCHỌN
    // ===============================
    
    private Boolean isAnonymous;               // Gửi ẩn danh
    private Boolean subscribeToUpdates;        // Đăng ký nhận cập nhật
    private Boolean acceptTerms;               // Chấp nhận điều khoản
} 