package com.grabdriver.fe.models;

import java.util.Date;

public class Transaction {
    private String id;
    private String shipperId;
    private String orderId; // Null nếu không liên quan đến đơn hàng
    private String type; // EARNING, COD_DEPOSIT, TOP_UP, COMMISSION, BONUS, TIP
    private long amount;
    private String description;
    private Date transactionDate;
    private String status; // PENDING, COMPLETED, FAILED
    
    // For earnings breakdown
    private long deliveryFee; // Phí giao hàng gốc
    private long commission; // Chiết khấu 15%
    private long netAmount; // Số tiền thực nhận
    private long tip; // Tip từ khách
    
    // Constructors
    public Transaction() {
        this.transactionDate = new Date();
        this.status = "COMPLETED";
    }
    
    public Transaction(String shipperId, String type, long amount, String description) {
        this();
        this.shipperId = shipperId;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }
    
    // Factory methods for different transaction types
    public static Transaction createEarningTransaction(String shipperId, String orderId, 
                                                     long deliveryFee, long tip) {
        Transaction transaction = new Transaction();
        transaction.setShipperId(shipperId);
        transaction.setOrderId(orderId);
        transaction.setType("EARNING");
        transaction.setDeliveryFee(deliveryFee);
        transaction.setTip(tip);
        
        // Calculate commission and net amount
        long commission = Math.round(deliveryFee * Wallet.COMMISSION_RATE);
        long netDeliveryFee = deliveryFee - commission;
        long totalNet = netDeliveryFee + tip;
        
        transaction.setCommission(commission);
        transaction.setNetAmount(totalNet);
        transaction.setAmount(totalNet);
        transaction.setDescription("Thu nhập từ đơn hàng #" + orderId);
        
        return transaction;
    }
    
    public static Transaction createCodDepositTransaction(String shipperId, long amount) {
        Transaction transaction = new Transaction(shipperId, "COD_DEPOSIT", amount, 
                                                "Nộp tiền COD vào tài khoản");
        return transaction;
    }
    
    public static Transaction createTopUpTransaction(String shipperId, long amount) {
        Transaction transaction = new Transaction(shipperId, "TOP_UP", amount, 
                                                "Nạp tiền vào tài khoản");
        return transaction;
    }
    
    public static Transaction createBonusTransaction(String shipperId, long amount, String reason) {
        Transaction transaction = new Transaction(shipperId, "BONUS", amount, 
                                                "Thưởng: " + reason);
        return transaction;
    }
    
    // Helper methods
    public String getTypeDisplayName() {
        switch (type) {
            case "EARNING":
                return "Thu nhập";
            case "COD_DEPOSIT":
                return "Nộp COD";
            case "TOP_UP":
                return "Nạp tiền";
            case "COMMISSION":
                return "Chiết khấu";
            case "BONUS":
                return "Thưởng";
            case "TIP":
                return "Tip";
            default:
                return type;
        }
    }
    
    public String getFormattedAmount() {
        String prefix = (amount >= 0) ? "+" : "";
        return prefix + String.format("%,d đ", amount);
    }
    
    public String getFormattedDeliveryFee() {
        return String.format("%,d đ", deliveryFee);
    }
    
    public String getFormattedCommission() {
        return String.format("-%,d đ", commission);
    }
    
    public String getFormattedNetAmount() {
        return String.format("%,d đ", netAmount);
    }
    
    public String getFormattedTip() {
        return tip > 0 ? String.format("+%,d đ", tip) : "0 đ";
    }
    
    public boolean isPositiveTransaction() {
        return "EARNING".equals(type) || "TOP_UP".equals(type) || 
               "BONUS".equals(type) || "TIP".equals(type) || "COD_DEPOSIT".equals(type);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getShipperId() { return shipperId; }
    public void setShipperId(String shipperId) { this.shipperId = shipperId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(long deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public long getCommission() { return commission; }
    public void setCommission(long commission) { this.commission = commission; }
    
    public long getNetAmount() { return netAmount; }
    public void setNetAmount(long netAmount) { this.netAmount = netAmount; }
    
    public long getTip() { return tip; }
    public void setTip(long tip) { this.tip = tip; }
} 