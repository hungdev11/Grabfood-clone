package com.api.service;

import com.api.dto.response.DashboardStatsResponse;
import com.api.dto.response.ShipperOrderResponse;
import org.springframework.data.domain.Page;

public interface DashboardService {

    /**
     * Lấy thống kê dashboard tổng quan cho shipper
     * Bao gồm thu nhập, đơn hàng, hiệu suất, v.v.
     */
    DashboardStatsResponse getDashboardStats(String shipperPhone);

    /**
     * Lấy danh sách đơn hàng gần vị trí shipper
     * Chỉ trả về các đơn chưa có shipper hoặc sẵn sàng assign
     */
    Page<ShipperOrderResponse> getNearbyOrders(
            String shipperPhone,
            double latitude,
            double longitude,
            double radiusKm,
            int page,
            int size);
}