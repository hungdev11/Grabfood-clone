package com.api.service.Imp.notificationstrategy;

import com.api.entity.Order;
import com.api.service.strategy.OrderStatusNotificationStrategy;

public class ProcessingNotificationStrategy implements OrderStatusNotificationStrategy {
    public String getSubject(Order order) {
        return "Đơn hàng đang được xử lý";
    }

    public String getBody(Order order) {
        return "Đơn hàng #" + order.getId() + " của bạn đang được xử lý. Vui lòng chờ xác nhận.";
    }
}
