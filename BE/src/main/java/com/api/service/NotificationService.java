package com.api.service;

import com.api.dto.response.OrderResponse;
import com.api.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNewOrderNotification(long restaurantId, Order order) {
        log.info("Sending message to order id {} to restaurant {}", order.getId(), restaurantId);
        log.info("Order id {}", order.getId());
        log.info("Restaurant id {}", restaurantId);
        var x = OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .build();
        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId, x);
    }
}
