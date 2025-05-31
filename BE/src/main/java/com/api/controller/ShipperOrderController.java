package com.api.controller;

import com.api.dto.request.RejectOrderRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.ShipperOrderResponse;
import com.api.entity.Order;
import com.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class ShipperOrderController {

    private final OrderService orderService;

    /**
     * GET /api/orders - Lấy danh sách đơn hàng (có filter theo trạng thái)
     * Trả về List để Android app dễ xử lý
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipperOrderResponse>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting orders for shipper: {}, status: {}, page: {}, size: {}",
                    phone, status, page, size);

            Page<ShipperOrderResponse> orders = orderService.getOrdersForShipper(phone, status, page, size);

            // Convert Page to List for Android compatibility
            List<ShipperOrderResponse> orderList = orders.getContent();

            return ResponseEntity.ok(ApiResponse.<List<ShipperOrderResponse>>builder()
                    .code(200)
                    .message("Success")
                    .data(orderList)
                    .build());

        } catch (Exception e) {
            log.error("Error getting orders for shipper", e);
            return ResponseEntity.status(500).body(ApiResponse.<List<ShipperOrderResponse>>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/orders/page - Lấy danh sách đơn hàng với pagination info (nếu cần)
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ShipperOrderResponse>>> getOrdersWithPagination(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting orders with pagination for shipper: {}, status: {}, page: {}, size: {}",
                    phone, status, page, size);

            Page<ShipperOrderResponse> orders = orderService.getOrdersForShipper(phone, status, page, size);

            return ResponseEntity.ok(ApiResponse.<Page<ShipperOrderResponse>>builder()
                    .code(200)
                    .message("Success")
                    .data(orders)
                    .build());

        } catch (Exception e) {
            log.error("Error getting orders for shipper", e);
            return ResponseEntity.status(500).body(ApiResponse.<Page<ShipperOrderResponse>>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/orders/{id} - Lấy chi tiết một đơn hàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipperOrderResponse>> getOrderDetail(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting order detail {} for shipper: {}", id, phone);

            ShipperOrderResponse order = orderService.getOrderDetailForShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<ShipperOrderResponse>builder()
                    .code(200)
                    .message("Success")
                    .data(order)
                    .build());

        } catch (Exception e) {
            log.error("Error getting order detail {}", id, e);
            return ResponseEntity.status(404).body(ApiResponse.<ShipperOrderResponse>builder()
                    .code(404)
                    .message("Order not found or access denied: " + e.getMessage())
                    .build());
        }
    }

    /**
     * PUT /api/orders/{id}/accept - Nhận đơn hàng
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<String>> acceptOrder(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} accepting order {}", phone, id);

            String result = orderService.acceptOrderByShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Order accepted successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error accepting order {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot accept order: " + e.getMessage())
                    .build());
        }
    }

    /**
     * PUT /api/orders/{id}/reject - Từ chối đơn hàng
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectOrder(
            @PathVariable Long id,
            @RequestBody RejectOrderRequest request) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} rejecting order {} with reason: {}", phone, id, request.getReason());

            String result = orderService.rejectOrderByShipper(id, phone, request.getReason());

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Order rejected successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error rejecting order {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot reject order: " + e.getMessage())
                    .build());
        }
    }

    /**
     * PUT /api/orders/{id}/pickup - Xác nhận đã lấy hàng
     */
    @PutMapping("/{id}/pickup")
    public ResponseEntity<ApiResponse<String>> pickupOrder(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} picking up order {}", phone, id);

            String result = orderService.pickupOrderByShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Order picked up successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error picking up order {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot pickup order: " + e.getMessage())
                    .build());
        }
    }

    /**
     * PUT /api/orders/{id}/complete - Xác nhận đã giao hàng thành công
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<String>> completeOrder(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} completing order {}", phone, id);

            String result = orderService.completeOrderByShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Order completed successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error completing order {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot complete order: " + e.getMessage())
                    .build());
        }
    }

    /**
     * PUT /api/orders/{id}/cancel - Hủy đơn hàng
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} cancelling order {}", phone, id);

            String result = orderService.cancelOrderByShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Order cancelled successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error cancelling order {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot cancel order: " + e.getMessage())
                    .build());
        }
    }
}