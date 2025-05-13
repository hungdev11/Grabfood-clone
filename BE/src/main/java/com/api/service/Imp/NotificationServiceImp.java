package com.api.service.Imp;

import com.api.dto.response.NotificationResponse;
import com.api.entity.*;
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
    private final RestaurantService restaurantService;
    private final List<NotificationStrategy> strategies;

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

    public void sendNewOrderNotification(long restaurantId) {
        log.info("Sending message to restaurant {}", restaurantId);
        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId, "");
    }

    @Override
    public List<NotificationResponse> fetchNotificationsPopup(long restaurantId) {
        log.info("Fetching notifications for restaurant {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        log.info("fff {}", restaurant.getAccount().getNotificationDetails().size());
        return restaurant.getAccount().getNotificationDetails()
                .stream()
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
