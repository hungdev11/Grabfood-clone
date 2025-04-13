package com.api.service;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.ApplyVoucherResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    List<CartDetail> getCartDetailsByOrder(Long orderId, String status);
    List<Order> getOrdersByUser(Long userId);

   ApplyVoucherResponse applyVoucherToOrder(ApplyVoucherRequest request);
}
