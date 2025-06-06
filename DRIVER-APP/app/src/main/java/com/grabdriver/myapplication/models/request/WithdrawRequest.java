package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class WithdrawRequest {
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("bankAccount")
    private String bankAccount;
    
    @SerializedName("bankName")
    private String bankName;
    
    @SerializedName("note")
    private String note;

    // Constructor
    public WithdrawRequest(double amount, String bankAccount, String bankName, String note) {
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.note = note;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 