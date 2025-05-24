package com.api.service;

import com.api.dto.response.NotificationResponse;
import com.api.entity.Account;
import com.api.entity.Order;
import com.api.entity.Restaurant;
import com.api.utils.NotificationType;

import java.util.List;

public interface NotificationService {
    long createNewNotification(Account account, String subject, String body, NotificationType type);
    void sendNewOrderNotification(long restaurantId);
    void sendUserNotificationWhenOrderStatusChanged(long userId);
    public void sendDeliveryGuyNotificationWhenOrderStatusChanged(long deliveryGuyId);
    public void sendAdminNotification(long userId);
    List<NotificationResponse> fetchNotificationsPopup(Account account);
    void markAsRead(long accountNotificationId);
    void markDeleted(long accountNotificationId);
    void markAllAsRead(Account account);
    void markAllAsDeleted(Account account);
}
