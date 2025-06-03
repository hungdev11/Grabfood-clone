package com.grabdriver.myapplication.models;

public class LoginResponse {
    private String token;
    private Long shipperId;
    private Shipper shipperInfo;
    private boolean success;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long shipperId, Shipper shipperInfo) {
        this.token = token;
        this.shipperId = shipperId;
        this.shipperInfo = shipperInfo;
        this.success = true;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public Shipper getShipperInfo() {
        return shipperInfo;
    }

    public void setShipperInfo(Shipper shipperInfo) {
        this.shipperInfo = shipperInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "shipperId=" + shipperId +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", hasToken=" + (token != null && !token.isEmpty()) +
                '}';
    }
}