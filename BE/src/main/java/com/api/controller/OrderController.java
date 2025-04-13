package com.api.controller;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.ApplyVoucherResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;
import com.api.repository.OrderRepository;
import com.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    OrderService orderService;
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getListOrders(@PathVariable Long userId) {
        List<Order> order = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(order);
    }
    @GetMapping("/{orderId}/cart-details")
    public ResponseEntity<List<CartDetail>> getListCartDetails(@PathVariable Long orderId) {
        List<CartDetail> cartDetails = orderService.getCartDetailsByOrder(orderId, "PENDING");
        return ResponseEntity.ok(cartDetails);
    }

    @PostMapping("/check/applyVoucher")
    public ApiResponse<ApplyVoucherResponse> applyVoucherToOrder(@RequestBody ApplyVoucherRequest request) {
        return ApiResponse.<ApplyVoucherResponse>builder()
                .data(orderService.applyVoucherToOrder(request))
                .code(200)
                .message("Success")
                .build();
    }
}
