package com.grabdriver.myapplication.models;

import java.util.Date;

public class Transaction {
    private long id;
    private long amount;
    private Long commission;
    private Long deliveryFee;
    private String description;
    private Long netAmount;
    private Long orderId;
    private String status; // COMPLETED, FAILED, PENDING
    private Long tip;
    private Date transactionDate;
    private String type; // BONUS, COD_DEPOSIT, COMMISSION, EARNING, TIP, TOP_UP
    private long shipperId;

    // Constructors
    public Transaction() {
    }

    public Transaction(long id, long amount, String type, String status, Date transactionDate, String description) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Long getCommission() {
        return commission;
    }

    public void setCommission(Long commission) {
        this.commission = commission;
    }

    public Long getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Long deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Long netAmount) {
        this.netAmount = netAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTip() {
        return tip;
    }

    public void setTip(Long tip) {
        this.tip = tip;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getShipperId() {
        return shipperId;
    }

    public void setShipperId(long shipperId) {
        this.shipperId = shipperId;
    }
}