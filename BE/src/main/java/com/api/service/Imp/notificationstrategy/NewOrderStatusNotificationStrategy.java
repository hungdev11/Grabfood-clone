package com.api.service.Imp.notificationstrategy;

import com.api.entity.Order;
import com.api.service.strategy.OrderStatusNotificationStrategy;

public class NewOrderStatusNotificationStrategy implements OrderStatusNotificationStrategy {
    @Override
    public String getSubject(Order order) {
        return "Có đơn hàng mới";
    }
    @Override
    public String getBody(Order order) {
        return "Mã đơn #" + order.getId() + " , kiểm tra ngay!!!";
    }
}
