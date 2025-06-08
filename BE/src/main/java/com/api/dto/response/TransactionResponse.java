package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO cho thông tin giao dịch của driver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private Long transactionId;            // ID giao dịch
    private Long shipperId;                // ID shipper
    private Long orderId;                  // ID đơn hàng (nếu có)
    
    // Thông tin giao dịch
    private String type;                   // Loại giao dịch (EARNING, WITHDRAWAL, TIP, etc.)
    private String status;                 // Trạng thái (PENDING, COMPLETED, FAILED)
    private Long amount;                   // Số tiền (VND)
    private String description;            // Mô tả giao dịch
    private String note;                   // Ghi chú
    
    // Thông tin đơn hàng liên quan
    private String orderCode;              // Mã đơn hàng
    private String customerName;           // Tên khách hàng
    private String restaurantName;         // Tên nhà hàng
    
    // Thông tin thời gian
    private LocalDateTime transactionDate; // Thời gian giao dịch
    private LocalDateTime completedDate;   // Thời gian hoàn thành
    private LocalDateTime createdAt;       // Thời gian tạo
    
    // Thông tin ngân hàng (cho withdrawal)
    private String bankName;               // Tên ngân hàng
    private String bankAccountNumber;      // Số tài khoản (masked)
    private String transferReference;      // Mã tham chiếu chuyển khoản
    
    // Số dư sau giao dịch
    private Long balanceBefore;            // Số dư trước giao dịch
    private Long balanceAfter;             // Số dư sau giao dịch
    
    // Thông tin bổ sung
    private String paymentMethod;          // Phương thức thanh toán
    private String currency;               // Loại tiền tệ
    private Boolean isRefundable;          // Có thể hoàn tiền không
    private LocalDateTime refundableUntil; // Có thể hoàn tiền đến khi nào
} 