package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.DashboardStatsResponse;
import com.api.dto.response.ShipperOrderResponse;
import com.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/stats - Thống kê tổng quan hôm nay
     * Thu nhập, số đơn hàng, rating, km đã đi...
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting dashboard stats for shipper: {}", phone);

            DashboardStatsResponse stats = dashboardService.getDashboardStats(phone);

            return ResponseEntity.ok(ApiResponse.<DashboardStatsResponse>builder()
                    .code(200)
                    .message("Success")
                    .data(stats)
                    .build());

        } catch (Exception e) {
            log.error("Error getting dashboard stats", e);
            return ResponseEntity.status(500).body(ApiResponse.<DashboardStatsResponse>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/dashboard/nearby-orders - Lấy danh sách đơn hàng gần vị trí shipper
     * Yêu cầu shipper gửi vị trí hiện tại qua query params
     */
    @GetMapping("/nearby-orders")
    public ResponseEntity<ApiResponse<Page<ShipperOrderResponse>>> getNearbyOrders(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting nearby orders for shipper: {} at location ({}, {}) within {}km",
                    phone, latitude, longitude, radiusKm);

            Page<ShipperOrderResponse> nearbyOrders = dashboardService.getNearbyOrders(
                    phone, latitude, longitude, radiusKm, page, size);

            return ResponseEntity.ok(ApiResponse.<Page<ShipperOrderResponse>>builder()
                    .code(200)
                    .message("Success")
                    .data(nearbyOrders)
                    .build());

        } catch (Exception e) {
            log.error("Error getting nearby orders", e);
            return ResponseEntity.status(500).body(ApiResponse.<Page<ShipperOrderResponse>>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }
}