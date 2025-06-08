package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class EarningsResponse {
    @SerializedName("walletId")
    private long walletId;
    
    @SerializedName("shipperId")
    private long shipperId;
    
    @SerializedName("shipperName")
    private String shipperName;
    
    @SerializedName("currentBalance")
    private long currentBalance;
    
    @SerializedName("todayEarnings")
    private long todayEarnings;
    
    @SerializedName("weekEarnings")
    private long weekEarnings;
    
    @SerializedName("monthEarnings")
    private long monthEarnings;
    
    @SerializedName("totalEarnings")
    private long totalEarnings;
    
    @SerializedName("codHolding")
    private long codHolding;
    
    @SerializedName("pendingAmount")
    private long pendingAmount;
    
    @SerializedName("withdrawableAmount")
    private long withdrawableAmount;
    
    @SerializedName("totalTransactions")
    private int totalTransactions;
    
    @SerializedName("pendingTransactions")
    private int pendingTransactions;
    
    @SerializedName("completedTransactions")
    private int completedTransactions;
    
    @SerializedName("lastUpdated")
    private Date lastUpdated;
    
    @SerializedName("lastEarning")
    private Date lastEarning;
    
    @SerializedName("lastWithdrawal")
    private Date lastWithdrawal;
    
    @SerializedName("bankName")
    private String bankName;
    
    @SerializedName("bankAccountNumber")
    private String bankAccountNumber;
    
    @SerializedName("bankAccountHolder")
    private String bankAccountHolder;
    
    @SerializedName("isVerified")
    private boolean isVerified;
    
    @SerializedName("dailyWithdrawLimit")
    private long dailyWithdrawLimit;
    
    @SerializedName("monthlyWithdrawLimit")
    private long monthlyWithdrawLimit;
    
    @SerializedName("autoWithdraw")
    private boolean autoWithdraw;
    
    @SerializedName("autoWithdrawThreshold")
    private long autoWithdrawThreshold;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("currency")
    private String currency;

    // Constructors
    public EarningsResponse() {
    }

    // Getters and Setters
    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public long getShipperId() {
        return shipperId;
    }

    public void setShipperId(long shipperId) {
        this.shipperId = shipperId;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        this.currentBalance = currentBalance;
    }

    public long getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(long todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public long getWeekEarnings() {
        return weekEarnings;
    }

    public void setWeekEarnings(long weekEarnings) {
        this.weekEarnings = weekEarnings;
    }

    public long getMonthEarnings() {
        return monthEarnings;
    }

    public void setMonthEarnings(long monthEarnings) {
        this.monthEarnings = monthEarnings;
    }

    public long getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(long totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public long getCodHolding() {
        return codHolding;
    }

    public void setCodHolding(long codHolding) {
        this.codHolding = codHolding;
    }

    public long getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(long pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public long getWithdrawableAmount() {
        return withdrawableAmount;
    }

    public void setWithdrawableAmount(long withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(int pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public int getCompletedTransactions() {
        return completedTransactions;
    }

    public void setCompletedTransactions(int completedTransactions) {
        this.completedTransactions = completedTransactions;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastEarning() {
        return lastEarning;
    }

    public void setLastEarning(Date lastEarning) {
        this.lastEarning = lastEarning;
    }

    public Date getLastWithdrawal() {
        return lastWithdrawal;
    }

    public void setLastWithdrawal(Date lastWithdrawal) {
        this.lastWithdrawal = lastWithdrawal;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }

    public void setBankAccountHolder(String bankAccountHolder) {
        this.bankAccountHolder = bankAccountHolder;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public long getDailyWithdrawLimit() {
        return dailyWithdrawLimit;
    }

    public void setDailyWithdrawLimit(long dailyWithdrawLimit) {
        this.dailyWithdrawLimit = dailyWithdrawLimit;
    }

    public long getMonthlyWithdrawLimit() {
        return monthlyWithdrawLimit;
    }

    public void setMonthlyWithdrawLimit(long monthlyWithdrawLimit) {
        this.monthlyWithdrawLimit = monthlyWithdrawLimit;
    }

    public boolean isAutoWithdraw() {
        return autoWithdraw;
    }

    public void setAutoWithdraw(boolean autoWithdraw) {
        this.autoWithdraw = autoWithdraw;
    }

    public long getAutoWithdrawThreshold() {
        return autoWithdrawThreshold;
    }

    public void setAutoWithdrawThreshold(long autoWithdrawThreshold) {
        this.autoWithdrawThreshold = autoWithdrawThreshold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 