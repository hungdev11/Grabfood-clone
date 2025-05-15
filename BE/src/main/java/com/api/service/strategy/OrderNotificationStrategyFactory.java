package com.api.service.strategy;

import com.api.service.Imp.notificationstrategy.NewOrderStatusNotificationStrategy;
import com.api.service.Imp.notificationstrategy.ProcessingNotificationStrategy;
import com.api.service.Imp.notificationstrategy.RejectedNotificationStrategy;
import com.api.service.Imp.notificationstrategy.ShipppingNotificationStrategy;
import com.api.utils.OrderStatus;

public class OrderNotificationStrategyFactory {
    public static OrderStatusNotificationStrategy getStrategy(OrderStatus status) {
        return switch (status) {
            case PENDING -> new NewOrderStatusNotificationStrategy();
            case PROCESSING -> new ProcessingNotificationStrategy();
            case REJECTED -> new RejectedNotificationStrategy();
            case SHIPPING -> new ShipppingNotificationStrategy();
            default -> throw new IllegalArgumentException("Unsupported order status: " + status);
        };
    }
}
