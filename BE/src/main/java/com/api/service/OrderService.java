package com.api.service;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.ApplyVoucherResponse;
import com.api.dto.response.GetOrderGroupResponse;
import com.api.dto.response.OrderResponse;
import com.api.dto.response.PageResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;

import com.api.utils.OrderStatus;
import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    List<CartDetail> getCartDetailsByOrder(Long orderId, String status);
    List<Order> getOrdersByUser(Long userId);
    ApplyVoucherResponse applyVoucherToOrder(ApplyVoucherRequest request);
    void DeleteOrderFailedPayment(Long orderId);
    List<OrderResponse> getUserOrderByStatus(Long userId, OrderStatus status);
    List<OrderResponse> getUserOrder(Long userId);
    PageResponse<GetOrderGroupResponse> getRestaurantOrders(long restaurantId, int page, int size, String status);
    Order getOrderById(Long orderId);
    List<Order> listAllOrdersOfRestaurant(Long restaurantId);
    void updateOrderStatus(Order order, OrderStatus status);
}
