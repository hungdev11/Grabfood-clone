package com.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;
    
    @Column(nullable = false)
    private Long amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column
    private String description;
    
    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "commission")
    private Long commission;
    
    @Column(name = "delivery_fee")
    private Long deliveryFee;
    
    @Column(name = "tip")
    private Long tip;
    
    @Column(name = "net_amount")
    private Long netAmount;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "note")
    private String note;
    
    // Enum cho loại transaction
    public enum TransactionType {
        EARNING,        // Thu nhập từ giao hàng
        TIP,           // Tiền tip từ khách hàng
        BONUS,         // Thưởng từ hệ thống
        COMMISSION,    // Hoa hồng platform
        COD_DEPOSIT,   // Nộp tiền COD
        TOP_UP         // Rút tiền
    }
    
    // Enum cho trạng thái transaction
    public enum TransactionStatus {
        PENDING,    // Đang xử lý
        COMPLETED,  // Hoàn thành
        FAILED      // Thất bại
    }
    
    // Phương thức tiện ích
    public void completeTransaction() {
        this.status = TransactionStatus.COMPLETED;
    }
    
    public void failTransaction() {
        this.status = TransactionStatus.FAILED;
    }
    
    public Boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    }
    
    public Boolean isPending() {
        return this.status == TransactionStatus.PENDING;
    }
    
    public Boolean isFailed() {
        return this.status == TransactionStatus.FAILED;
    }
    
    public Boolean isEarningType() {
        return this.type == TransactionType.EARNING;
    }
    
    public Boolean isTipType() {
        return this.type == TransactionType.TIP;
    }
    
    public Boolean isBonusType() {
        return this.type == TransactionType.BONUS;
    }
    
    public Boolean isCodType() {
        return this.type == TransactionType.COD_DEPOSIT;
    }
    
    public Boolean isWithdrawalType() {
        return this.type == TransactionType.TOP_UP;
    }
    
    public Long getShipperId() {
        return shipper != null ? shipper.getId() : null;
    }
    
    // Static factory methods để tạo các loại transaction
    public static Transaction createEarningTransaction(Shipper shipper, Long orderId, 
                                                     Long deliveryFee, Long commission, 
                                                     Long tip, String description) {
        Long netAmount = deliveryFee - commission + (tip != null ? tip : 0);
        
        return Transaction.builder()
                .shipper(shipper)
                .orderId(orderId)
                .amount(deliveryFee)
                .deliveryFee(deliveryFee)
                .commission(commission)
                .tip(tip != null ? tip : 0)
                .netAmount(netAmount)
                .type(TransactionType.EARNING)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();
    }
    
    public static Transaction createTipTransaction(Shipper shipper, Long orderId, 
                                                  Long tipAmount, String description) {
        return Transaction.builder()
                .shipper(shipper)
                .orderId(orderId)
                .amount(tipAmount)
                .tip(tipAmount)
                .netAmount(tipAmount)
                .type(TransactionType.TIP)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();
    }
    
    public static Transaction createBonusTransaction(Shipper shipper, Long bonusAmount, 
                                                    String description) {
        return Transaction.builder()
                .shipper(shipper)
                .amount(bonusAmount)
                .netAmount(bonusAmount)
                .type(TransactionType.BONUS)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();
    }
    
    public static Transaction createCodTransaction(Shipper shipper, Long orderId, 
                                                  Long codAmount, String description) {
        return Transaction.builder()
                .shipper(shipper)
                .orderId(orderId)
                .amount(codAmount)
                .netAmount(codAmount)
                .type(TransactionType.COD_DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();
    }
    
    public static Transaction createWithdrawalTransaction(Shipper shipper, Long amount, 
                                                         String paymentMethod, String description) {
        return Transaction.builder()
                .shipper(shipper)
                .amount(amount)
                .netAmount(-amount) // Số âm vì rút tiền
                .type(TransactionType.TOP_UP)
                .status(TransactionStatus.PENDING)
                .paymentMethod(paymentMethod)
                .description(description)
                .build();
    }
    
    public static Transaction createCommissionTransaction(Shipper shipper, Long orderId, 
                                                         Long commissionAmount, String description) {
        return Transaction.builder()
                .shipper(shipper)
                .orderId(orderId)
                .amount(commissionAmount)
                .commission(commissionAmount)
                .netAmount(-commissionAmount) // Số âm vì trừ commission
                .type(TransactionType.COMMISSION)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();
    }
} 