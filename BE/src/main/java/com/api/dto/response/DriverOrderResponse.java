package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverOrderResponse {

    private Long orderId;

    private String orderStatus;

    private LocalDateTime orderDate;

    private String customerName;

    private String customerPhone;

    private String pickupAddress;

    private String deliveryAddress;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    private BigDecimal totalPrice;

    private BigDecimal shippingFee;

    private BigDecimal deliveryDistance;

    private Integer estimatedTime;

    private String note;

    private String paymentMethod;

    private LocalDateTime assignedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime pickedUpAt;

    private LocalDateTime deliveredAt;

    private Long tip;

    private Integer gemsEarned;

    private BigDecimal shipperEarning;

    private String restaurantName;

    private String restaurantPhone;

    private String restaurantAddress;

    private List<OrderItemResponse> items;

    // Enum cho trạng thái đơn hàng
    public enum OrderStatus {
        PENDING, PROCESSING, READY_FOR_PICKUP, SHIPPING, COMPLETED, CANCELLED, REJECTED
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemResponse {
        private String foodName;
        private Integer quantity;
        private BigDecimal price;
        private String note;
        private List<String> additions;
    }
}