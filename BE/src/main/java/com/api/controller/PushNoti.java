package com.api.controller;

import com.api.entity.Order;
import com.api.service.NotificationService;
import com.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushNoti {
    private final NotificationService notificationService;
    private final OrderService orderService;

    @GetMapping("/push-noti/{resId}")
    public void pushNoti(@PathVariable long resId, Order order) {
        order = orderService.getOrderById(190L);
        notificationService.sendNewOrderNotification(resId, order);
    }
}
