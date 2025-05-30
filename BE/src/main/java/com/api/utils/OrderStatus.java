package com.api.utils;

public enum OrderStatus {
    PENDING, // Chờ restaurant xác nhận
    CANCELLED, // Đã hủy
    PROCESSING, // Restaurant đang chuẩn bị
    READY_FOR_PICKUP, // Sẵn sàng để shipper lấy
    SHIPPING, // Đang giao hàng
    COMPLETED, // Hoàn thành
    REJECTED // Bị từ chối
}
