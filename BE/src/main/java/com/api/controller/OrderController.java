package com.api.controller;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.ApplyVoucherResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.service.OrderService;
import com.api.service.UserService;
import com.api.utils.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    // đã chuyển qua payment
//    @PostMapping("/create")
//    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
//        OrderResponse order = orderService.createOrder(request);
//        return ResponseEntity.ok(order);
//    }
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

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrderByStatus(HttpServletRequest request, @RequestParam(value = "status", required = false) OrderStatus status) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Long userId = userService.getUserIdByPhoneOrEmail(username);
        List<OrderResponse> response;
        if(status != null) {
            response = orderService.getUserOrderByStatus(userId, status);
        } else {
            response = orderService.getUserOrder(userId);
        }
        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<List<OrderResponse>> getUserOrder(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
//        }
//
//        String token = authHeader.substring(7);
//        String username = jwtService.extractUsername(token);
//        Long userId = userService.getUserIdByPhoneOrEmail(username);
//        List<OrderResponse> response = orderService.getUserOrder(userId);
//        return ResponseEntity.ok(response);
//    }


    @PostMapping("/check/applyVoucher")
    public ApiResponse<ApplyVoucherResponse> applyVoucherToOrder(@RequestBody ApplyVoucherRequest request) {
        return ApiResponse.<ApplyVoucherResponse>builder()
                .data(orderService.applyVoucherToOrder(request))
                .code(200)
                .message("Success")
                .build();
    }
}
