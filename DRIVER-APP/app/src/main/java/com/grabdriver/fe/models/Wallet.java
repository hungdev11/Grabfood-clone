package com.grabdriver.fe.models;

import java.util.Date;

public class Wallet {
    private String shipperId;
    private long currentBalance; // Số dư hiện tại trong tài khoản GrabFood
    private long codHolding; // Tiền COD đang giữ (chưa nộp)
    private long todayEarnings; // Thu nhập hôm nay
    private long weekEarnings; // Thu nhập tuần này
    private long monthEarnings; // Thu nhập tháng này
    private long totalEarnings; // Tổng thu nhập tích lũy
    private boolean isEligibleForCOD; // Đủ điều kiện nhận đơn COD (≥500k)
    private Date lastUpdated;
    
    // Constants
    public static final long MINIMUM_BALANCE = 500000; // 500k VND
    public static final double COMMISSION_RATE = 0.15; // 15% chiết khấu
    
    // Constructors
    public Wallet() {
        this.currentBalance = 0;
        this.codHolding = 0;
        this.todayEarnings = 0;
        this.weekEarnings = 0;
        this.monthEarnings = 0;
        this.totalEarnings = 0;
        this.lastUpdated = new Date();
        updateEligibility();
    }
    
    public Wallet(String shipperId) {
        this();
        this.shipperId = shipperId;
    }
    
    // Business logic methods
    public void updateEligibility() {
        this.isEligibleForCOD = (currentBalance >= MINIMUM_BALANCE);
    }
    
    public long calculateNetEarning(long deliveryFee) {
        return Math.round(deliveryFee * (1 - COMMISSION_RATE));
    }
    
    public long getCommissionAmount(long deliveryFee) {
        return Math.round(deliveryFee * COMMISSION_RATE);
    }
    
    public String getAccountStatus() {
        if (isEligibleForCOD) {
            return "Đủ số dư - Nhận cả đơn Online + COD";
        } else {
            return "Thiếu số dư - Chỉ nhận đơn Online";
        }
    }
    
    public long getRequiredTopUp() {
        if (isEligibleForCOD) {
            return 0;
        }
        return MINIMUM_BALANCE - currentBalance;
    }
    
    // Getters and Setters
    public String getShipperId() { return shipperId; }
    public void setShipperId(String shipperId) { this.shipperId = shipperId; }
    
    public long getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(long currentBalance) { 
        this.currentBalance = currentBalance;
        updateEligibility();
    }
    
    public long getCodHolding() { return codHolding; }
    public void setCodHolding(long codHolding) { this.codHolding = codHolding; }
    
    public long getTodayEarnings() { return todayEarnings; }
    public void setTodayEarnings(long todayEarnings) { this.todayEarnings = todayEarnings; }
    
    public long getWeekEarnings() { return weekEarnings; }
    public void setWeekEarnings(long weekEarnings) { this.weekEarnings = weekEarnings; }
    
    public long getMonthEarnings() { return monthEarnings; }
    public void setMonthEarnings(long monthEarnings) { this.monthEarnings = monthEarnings; }
    
    public long getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(long totalEarnings) { this.totalEarnings = totalEarnings; }
    
    public boolean isEligibleForCOD() { return isEligibleForCOD; }
    public void setEligibleForCOD(boolean eligibleForCOD) { this.isEligibleForCOD = eligibleForCOD; }
    
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
    
    // Helper methods for formatting
    public String getFormattedBalance() {
        return String.format("%,d đ", currentBalance);
    }
    
    public String getFormattedCodHolding() {
        return String.format("%,d đ", codHolding);
    }
    
    public String getFormattedTodayEarnings() {
        return String.format("%,d đ", todayEarnings);
    }
    
    public String getFormattedWeekEarnings() {
        return String.format("%,d đ", weekEarnings);
    }
    
    public String getFormattedTotalEarnings() {
        return String.format("%,d đ", totalEarnings);
    }
} 