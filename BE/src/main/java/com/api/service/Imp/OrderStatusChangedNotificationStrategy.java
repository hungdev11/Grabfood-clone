package com.api.service.Imp;

import com.api.entity.Account;
import com.api.entity.AccountNotification;
import com.api.entity.Notification;
import com.api.repository.AccountNotificationRepository;
import com.api.repository.AccountRepository;
import com.api.repository.NotificationRepository;
import com.api.service.strategy.NotificationStrategy;
import com.api.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderStatusChangedNotificationStrategy implements NotificationStrategy {

    private final NotificationRepository notificationRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(NotificationType type) {
        return NotificationType.ORDER_STATUS_CHANGED.equals(type);
    }

    @Override
    @Transactional
    public long create(Account account, String subject, String body) {
        Notification notification = Notification.builder()
                .subject(subject)
                .body(body)
                .type(NotificationType.ORDER_STATUS_CHANGED)
                .date(LocalDateTime.now())
                .build();

        AccountNotification accountNotification = AccountNotification.builder()
                .notification(notification)
                .receivedAccount(account)
                .build();

        account.getNotificationDetails().add(accountNotification);
        notification.getNotificationDetails().add(accountNotification);

        notificationRepository.save(notification);
        accountNotificationRepository.save(accountNotification);
        accountRepository.save(account);

        return notification.getId();
    }
}

