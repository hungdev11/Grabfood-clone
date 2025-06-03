package com.grabdriver.myapplication.models;

import java.util.Date;

public class Wallet {
    private long id;
    private long codHolding;
    private long currentBalance;
    private boolean isEligibleForCod;
    private Date lastUpdated;
    private long monthEarnings;
    private long todayEarnings;
    private long totalEarnings;
    private long weekEarnings;
    private long shipperId;

    // Constructors
    public Wallet() {
    }

    public Wallet(long id, long currentBalance, long todayEarnings, long weekEarnings,
            long monthEarnings, long totalEarnings, long codHolding, boolean isEligibleForCod) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.todayEarnings = todayEarnings;
        this.weekEarnings = weekEarnings;
        this.monthEarnings = monthEarnings;
        this.totalEarnings = totalEarnings;
        this.codHolding = codHolding;
        this.isEligibleForCod = isEligibleForCod;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCodHolding() {
        return codHolding;
    }

    public void setCodHolding(long codHolding) {
        this.codHolding = codHolding;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        this.currentBalance = currentBalance;
    }

    public boolean isEligibleForCod() {
        return isEligibleForCod;
    }

    public void setEligibleForCod(boolean eligibleForCod) {
        isEligibleForCod = eligibleForCod;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getMonthEarnings() {
        return monthEarnings;
    }

    public void setMonthEarnings(long monthEarnings) {
        this.monthEarnings = monthEarnings;
    }

    public long getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(long todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public long getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(long totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public long getWeekEarnings() {
        return weekEarnings;
    }

    public void setWeekEarnings(long weekEarnings) {
        this.weekEarnings = weekEarnings;
    }

    public long getShipperId() {
        return shipperId;
    }

    public void setShipperId(long shipperId) {
        this.shipperId = shipperId;
    }
}