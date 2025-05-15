package com.api.service.strategy;

import com.api.entity.Order;

public interface OrderStatusNotificationStrategy {
    String getSubject(Order order);
    String getBody(Order order);
}

