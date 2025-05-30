package com.api.dto.response;

import com.api.utils.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperOrderResponse {

    private Long id;
    private String customerName;
    private String customerPhone;
    private String address;
    private String note;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private String paymentMethod;

    // Restaurant info
    private String restaurantName;
    private String restaurantPhone;
    private String restaurantAddress;
    private Double restaurantLatitude;
    private Double restaurantLongitude;

    // Delivery info
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private Float distance;
    private Integer estimatedTime;

    // Assignment info
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    // Earnings info
    private BigDecimal shipperEarning;
    private Long tip;
    private Integer gemsEarned;

    // Order items
    private List<OrderItemResponse> items;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String foodName;
        private Integer quantity;
        private BigDecimal price;
        private String note;
        private List<String> additionalFoods;
    }
}