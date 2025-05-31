package com.api.service;

import com.api.entity.Reminder;
import com.api.entity.User;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
    // Add to EmailService.java
    void sendRestaurantAccountInfo(String to, String username, String password);
    void sendReminderEmail(User user, Reminder reminder);
}
