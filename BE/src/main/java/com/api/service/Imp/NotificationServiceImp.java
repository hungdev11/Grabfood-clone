package com.api.service.Imp;

import com.api.dto.response.NotificationResponse;
import com.api.entity.*;
import com.api.repository.AccountNotificationRepository;
import com.api.service.NotificationService;
import com.api.service.RestaurantService;
import com.api.service.strategy.NotificationStrategy;
import com.api.utils.NotificationType;
import com.api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final List<NotificationStrategy> strategies;
    private final AccountNotificationRepository accountNotificationRepository;

    @Override
    public long createNewNotification(Account account, String subject, String body, NotificationType type) {
        return strategies.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .map(s -> {
                    log.info("Creating notification for type {}", type);
                    return s.create(account, subject, body);
                })
                .orElseGet(() -> {
                    log.warn("Unsupported notification type: {}", type);
                    return -1L;
                });
    }

    @Override
    public void sendNewOrderNotification(long restaurantId) {
        log.info("Sending message to restaurant {}", restaurantId);
        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId, "");
    }

    @Override
    public void sendUserNotificationWhenOrderStatusChanged(long userId) {
        log.info("Sending message to user {}", userId);
        messagingTemplate.convertAndSend("/topic/client/" + userId, "");
    }

    @Override
    public void sendDeliveryGuyNotificationWhenOrderStatusChanged(long deliveryGuyId) {
        log.info("Sending message to delivery guy {}", deliveryGuyId);
        messagingTemplate.convertAndSend("/topic/ship/" + deliveryGuyId, "");
    }

    @Override
    public void sendAdminNotification(long userId) {
        log.info("Sending message to admin {}", userId);
        messagingTemplate.convertAndSend("/topic/admin/" + userId, "");
    }

    @Override
    public void markAsRead(long accountNotificationId) {
        accountNotificationRepository.findById(accountNotificationId).ifPresent(an -> {
            an.setRead(true);
            accountNotificationRepository.save(an);
        });
    }

    @Override
    public void markDeleted(long accountNotificationId) {
        accountNotificationRepository.findById(accountNotificationId).ifPresent(an -> {
            an.setRead(true);
            an.setDeleted(true);
            accountNotificationRepository.save(an);
        });
    }

    @Override
    public void markAllAsRead(Account account) {
        List<AccountNotification> unread = account.getNotificationDetails().stream()
                .filter(an -> !an.isDeleted() && !an.isRead())
                .peek(an -> an.setRead(true))
                .toList();
        accountNotificationRepository.saveAll(unread);
    }


    @Override
    public void markAllAsDeleted(Account account) {
        List<AccountNotification> undeleted = account.getNotificationDetails().stream()
                .filter(an -> !an.isDeleted())
                .peek(an -> {
                    an.setDeleted(true);
                    an.setRead(true);
                })
                .toList();
        accountNotificationRepository.saveAll(undeleted);
    }

    @Override
    public List<NotificationResponse> fetchNotificationsPopup(Account account) {
        return account.getNotificationDetails()
                .stream()
                .filter(an ->  !an.isDeleted())
                .sorted((a,b) ->
                        b.getNotification().getDate().compareTo(a.getNotification().getDate()))
                .limit(7)
                .map(an -> NotificationResponse.builder()
                        .id(an.getId())
                        .subject(an.getNotification().getSubject())
                        .body(an.getNotification().getBody())
                        .timeArrived(TimeUtil.formatRelativeTime(an.getNotification().getDate()) + " trước")
                        .isRead(an.isRead())
                        .build())
                .toList();
    }
}
