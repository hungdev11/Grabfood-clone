package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO cho thông tin ví của driver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    
    private Long walletId;                 // ID ví
    private Long shipperId;                // ID shipper
    private String shipperName;            // Tên shipper
    
    // Số dư và thu nhập
    private Long currentBalance;           // Số dư hiện tại (VND)
    private Long todayEarnings;            // Thu nhập hôm nay
    private Long weekEarnings;             // Thu nhập tuần này
    private Long monthEarnings;            // Thu nhập tháng này
    private Long totalEarnings;            // Tổng thu nhập
    
    // COD và holdings
    private Long codHolding;               // Tiền COD đang giữ
    private Long pendingAmount;            // Số tiền đang chờ xử lý
    private Long withdrawableAmount;       // Số tiền có thể rút
    
    // Thống kê giao dịch
    private Integer totalTransactions;     // Tổng số giao dịch
    private Integer pendingTransactions;   // Giao dịch đang chờ
    private Integer completedTransactions; // Giao dịch hoàn thành
    
    // Thông tin cập nhật
    private LocalDateTime lastUpdated;     // Lần cập nhật cuối
    private LocalDateTime lastEarning;     // Lần thu nhập cuối
    private LocalDateTime lastWithdrawal;  // Lần rút tiền cuối
    
    // Thông tin ngân hàng (nếu có)
    private String bankName;               // Tên ngân hàng
    private String bankAccountNumber;      // Số tài khoản
    private String bankAccountHolder;      // Chủ tài khoản
    private Boolean isVerified;            // Đã xác thực chưa
    
    // Giới hạn và cài đặt
    private Long dailyWithdrawLimit;       // Giới hạn rút tiền hàng ngày
    private Long monthlyWithdrawLimit;     // Giới hạn rút tiền hàng tháng
    private Boolean autoWithdraw;          // Tự động rút tiền
    private Long autoWithdrawThreshold;    // Ngưỡng rút tiền tự động
    
    // Trạng thái
    private String status;                 // Trạng thái ví
    private String currency;               // Loại tiền tệ (VND)
} 