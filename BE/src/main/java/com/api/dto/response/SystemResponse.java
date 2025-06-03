package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO cho hệ thống system utilities
 * Dùng cho các tiện ích hệ thống trong Phase 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemResponse {
    
    // ===============================
    // THÔNG TIN PHIÊN BẢN
    // ===============================
    
    private String currentVersion;             // Phiên bản hiện tại của app
    private String latestVersion;              // Phiên bản mới nhất
    private Boolean needsUpdate;               // Có cần cập nhật không
    private Boolean forceUpdate;               // Bắt buộc cập nhật không
    private String updateUrl;                  // Link download phiên bản mới
    private String changelog;                  // Thông tin thay đổi
    private Integer buildNumber;               // Số build
    
    // ===============================
    // THÔNG TIN HỖ TRỢ
    // ===============================
    
    private String supportHotline;             // Hotline hỗ trợ
    private String supportEmail;               // Email hỗ trợ
    private String faqUrl;                     // Link FAQ
    private String userGuideUrl;               // Link hướng dẫn sử dụng
    private List<ContactInfo> supportContacts; // Danh sách liên hệ hỗ trợ
    private List<FAQItem> frequentQuestions;   // Câu hỏi thường gặp
    
    // ===============================
    // THÔNG TIN HỆ THỐNG
    // ===============================
    
    private String systemStatus;               // Trạng thái hệ thống (ONLINE, MAINTENANCE, DOWN)
    private String maintenanceMessage;         // Thông báo bảo trì
    private LocalDateTime maintenanceStart;    // Thời gian bắt đầu bảo trì
    private LocalDateTime maintenanceEnd;      // Thời gian kết thúc bảo trì
    private List<SystemNotice> systemNotices; // Thông báo hệ thống
    
    // ===============================
    // FEEDBACK RESPONSE
    // ===============================
    
    private Long feedbackId;                   // ID feedback (sau khi gửi)
    private String feedbackStatus;             // Trạng thái feedback
    private String responseMessage;            // Tin nhắn phản hồi
    private LocalDateTime submittedAt;         // Thời gian gửi
    private String ticketNumber;               // Số ticket hỗ trợ
    
    // ===============================
    // CẤU HÌNH ỨNG DỤNG
    // ===============================
    
    private AppConfig appConfig;               // Cấu hình ứng dụng
    private List<FeatureFlag> featureFlags;    // Các tính năng bật/tắt
    private EmergencyConfig emergencyConfig;   // Cấu hình khẩn cấp
    
    // ===============================
    // NESTED CLASSES
    // ===============================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String type;                   // Loại liên hệ (PHONE, EMAIL, CHAT)
        private String label;                  // Nhãn hiển thị
        private String value;                  // Giá trị liên hệ
        private String description;            // Mô tả
        private Boolean isAvailable;           // Có sẵn không
        private String availableHours;         // Giờ hoạt động
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FAQItem {
        private String question;               // Câu hỏi
        private String answer;                 // Câu trả lời
        private String category;               // Danh mục
        private Integer priority;              // Độ ưu tiên
        private List<String> tags;             // Tags để tìm kiếm
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemNotice {
        private String title;                  // Tiêu đề thông báo
        private String message;                // Nội dung thông báo
        private String type;                   // Loại (INFO, WARNING, ERROR, SUCCESS)
        private String priority;               // Độ ưu tiên (HIGH, MEDIUM, LOW)
        private LocalDateTime startTime;       // Thời gian bắt đầu hiển thị
        private LocalDateTime endTime;         // Thời gian kết thúc hiển thị
        private Boolean isDismissible;         // Có thể đóng được không
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppConfig {
        private Integer orderRefreshInterval;  // Thời gian refresh danh sách đơn (giây)
        private Integer locationUpdateInterval; // Thời gian cập nhật vị trí (giây)
        private Integer maxOrderRadius;        // Bán kính nhận đơn tối đa (km)
        private Boolean enablePushNotifications; // Bật thông báo push
        private Boolean enableLocationTracking; // Bật theo dõi vị trí
        private String mapProvider;            // Nhà cung cấp bản đồ (GOOGLE, MAPBOX)
        private List<String> supportedLanguages; // Danh sách ngôn ngữ hỗ trợ
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureFlag {
        private String featureName;            // Tên tính năng
        private Boolean isEnabled;             // Có bật không
        private String description;            // Mô tả tính năng
        private String targetAudience;         // Đối tượng áp dụng
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyConfig {
        private String emergencyHotline;       // Hotline khẩn cấp
        private String emergencyMessage;       // Thông báo khẩn cấp
        private Boolean isEmergencyMode;       // Có trong chế độ khẩn cấp không
        private List<String> emergencyContacts; // Danh sách liên hệ khẩn cấp
    }
} 