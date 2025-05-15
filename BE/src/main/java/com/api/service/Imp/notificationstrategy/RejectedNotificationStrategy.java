package com.api.service.Imp.notificationstrategy;

import com.api.entity.Order;
import com.api.service.strategy.OrderStatusNotificationStrategy;

public class RejectedNotificationStrategy implements OrderStatusNotificationStrategy {
    public String getSubject(Order order) {
        return "Đơn hàng bị từ chối";
    }

    public String getBody(Order order) {
        return "Đơn hàng #" + order.getId() + " của bạn đã bị từ chối. Vui lòng kiểm tra lại.";
    }
}
