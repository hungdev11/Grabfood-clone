package com.api.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
    // Add to EmailService.java
    void sendRestaurantAccountInfo(String to, String username, String password);
}
