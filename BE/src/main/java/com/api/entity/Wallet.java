package com.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false, unique = true)
    private Shipper shipper;
    
    @Column(name = "current_balance", nullable = false)
    private Long currentBalance = 0L;
    
    @Column(name = "total_earnings", nullable = false)
    private Long totalEarnings = 0L;
    
    @Column(name = "today_earnings", nullable = false)
    private Long todayEarnings = 0L;
    
    @Column(name = "week_earnings", nullable = false)
    private Long weekEarnings = 0L;
    
    @Column(name = "month_earnings", nullable = false)
    private Long monthEarnings = 0L;
    
    @Column(name = "cod_holding", nullable = false)
    private Long codHolding = 0L;
    
    @Column(name = "is_eligible_for_cod", nullable = false)
    private Boolean isEligibleForCod = true;
    
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    // Phương thức quản lý số dư
    public void addBalance(Long amount) {
        if (amount > 0) {
            this.currentBalance += amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    public void deductBalance(Long amount) {
        if (amount > 0 && this.currentBalance >= amount) {
            this.currentBalance -= amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    public Boolean canWithdraw(Long amount) {
        return this.currentBalance >= amount && amount > 0;
    }
    
    // Phương thức quản lý earnings
    public void addEarnings(Long amount) {
        if (amount > 0) {
            this.currentBalance += amount;
            this.totalEarnings += amount;
            this.todayEarnings += amount;
            this.weekEarnings += amount;
            this.monthEarnings += amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    // Phương thức quản lý COD
    public void addCodHolding(Long amount) {
        if (amount > 0) {
            this.codHolding += amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    public void deductCodHolding(Long amount) {
        if (amount > 0 && this.codHolding >= amount) {
            this.codHolding -= amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    public Boolean canHandleCod(Long amount) {
        return this.isEligibleForCod && amount > 0;
    }
    
    // Reset earnings theo kỳ
    public void resetDailyEarnings() {
        this.todayEarnings = 0L;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void resetWeeklyEarnings() {
        this.weekEarnings = 0L;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void resetMonthlyEarnings() {
        this.monthEarnings = 0L;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Phương thức tiện ích
    public Long getAvailableBalance() {
        return this.currentBalance;
    }
    
    public Long getTotalCodHolding() {
        return this.codHolding;
    }
    
    public Boolean hasBalance() {
        return this.currentBalance > 0;
    }
    
    public Boolean hasCodHolding() {
        return this.codHolding > 0;
    }
    
    public Long getShipperId() {
        return shipper != null ? shipper.getId() : null;
    }
    
    // Phương thức xử lý transaction earnings
    public void processDeliveryEarning(Long deliveryFee, Long tip, Float commissionRate) {
        // Tính commission platform lấy (thường 15%)
        Long commission = (long) Math.round(deliveryFee * commissionRate / 100.0f);
        Long netEarning = deliveryFee - commission + tip;
        
        addEarnings(netEarning);
    }
    
    public void processWithdrawal(Long amount) {
        if (canWithdraw(amount)) {
            deductBalance(amount);
        }
    }
    
    public void processCodDeposit(Long codAmount) {
        if (canHandleCod(codAmount)) {
            addCodHolding(codAmount);
        }
    }
    
    public void processCodSubmission(Long codAmount) {
        if (codHolding >= codAmount) {
            deductCodHolding(codAmount);
        }
    }
} 