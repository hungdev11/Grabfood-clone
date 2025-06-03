package com.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để cập nhật profile của driver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @Size(min = 2, max = 100, message = "Tên phải từ 2-100 ký tự")
    private String name;                   // Tên shipper
    
    @Email(message = "Email không hợp lệ")
    private String email;                  // Email
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String emergencyContact;       // Liên hệ khẩn cấp
    
    private String address;                // Địa chỉ
    private String vehicleType;            // Loại phương tiện
    
    @Pattern(regexp = "^[A-Z0-9-]{6,15}$", message = "Biển số xe không hợp lệ")
    private String licensePlate;           // Biển số xe
    
    private String profileImageUrl;        // URL ảnh đại diện
    
    // Thông tin ngân hàng
    private String bankName;               // Tên ngân hàng
    private String bankAccountNumber;      // Số tài khoản
    private String bankAccountHolder;      // Chủ tài khoản
    
    // Cài đặt thông báo
    private Boolean enablePushNotifications;    // Bật thông báo push
    private Boolean enableEmailNotifications;   // Bật thông báo email
    private Boolean enableSmsNotifications;     // Bật thông báo SMS
    
    // Cài đặt ví
    private Boolean autoWithdraw;          // Tự động rút tiền
    private Long autoWithdrawThreshold;    // Ngưỡng rút tiền tự động
    
    // Cài đặt làm việc
    private String preferredWorkingHours;  // Giờ làm việc ưa thích
    private String[] preferredAreas;       // Khu vực làm việc ưa thích
} 