package com.api.service.Imp.notificationstrategy;

import com.api.entity.Order;
import com.api.service.strategy.NotificationStrategy;
import com.api.service.strategy.OrderStatusNotificationStrategy;

public class ShipppingNotificationStrategy implements OrderStatusNotificationStrategy {
    public String getSubject(Order order) {
        return "Đơn hàng đang được giao";
    }

    public String getBody(Order order) {
        return "Đơn hàng #" + order.getId() + " đang trên đường giao đến bạn. Theo dõi đơn hàng nhé!";
    }
}
