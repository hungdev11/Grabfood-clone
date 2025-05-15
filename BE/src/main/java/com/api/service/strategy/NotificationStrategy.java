package com.api.service.strategy;

import com.api.entity.Account;
import com.api.utils.NotificationType;

public interface NotificationStrategy {
    boolean supports(NotificationType type);
    long create(Account account, String subject, String body);
}

