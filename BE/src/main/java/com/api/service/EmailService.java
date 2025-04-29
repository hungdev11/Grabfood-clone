package com.api.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}
