package com.api.controller;

import com.api.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Gửi thông báo đơn mới đến nhà hàng dưới dạng chuỗi văn bản
    public void sendNewOrderNotification(long restaurantId, Order order) {
        String orderMessage = UUID.randomUUID() + "New order for restaurant " + restaurantId; // Chuỗi văn bản mock
        log.info("Sending message to order {}", orderMessage);
        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId, orderMessage);
    }
}
