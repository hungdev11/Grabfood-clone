package com.api.mapper;

import com.api.dto.response.OrderResponse;
import com.api.entity.Order;

public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
}
