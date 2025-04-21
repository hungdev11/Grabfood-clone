package com.api.repository;

import com.api.entity.Order;
import com.api.utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrderByUserIdAndStatus(Long user_id, OrderStatus status);

    List<Order> getOrderByUserId(Long user_id);
    @Procedure(name = "Order.getAllOrdersOfRestaurant")
    List<Long> getAllOrdersOfRestaurant(@Param("restaurant_id") Long restaurantId);
}
