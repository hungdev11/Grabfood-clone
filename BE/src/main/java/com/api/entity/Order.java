package com.api.entity;

import com.api.utils.OrderStatus;
import com.api.utils.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@NamedStoredProcedureQuery(name = "Order.getAllOrdersOfRestaurant", procedureName = "get_all_orders_of_restaurant", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "restaurant_id", type = Long.class)
})
@Table(name = "orders") // order conflict with ORDER in mysql
public class Order extends BaseEntity {

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false, precision = 11, scale = 2)
        private BigDecimal totalPrice;

        @Column(nullable = false)
        private String address;

        @Column(nullable = false)
        private LocalDateTime orderDate;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private OrderStatus status;

        private String note;

        @Column(nullable = false, precision = 9, scale = 2)
        private BigDecimal shippingFee;

        @Column(nullable = false, precision = 9, scale = 2)
        private BigDecimal discountShippingFee;

        @Column(nullable = false, precision = 9, scale = 2)
        private BigDecimal discountOrderPrice;

        @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
        @JsonIgnore
        private List<CartDetail> cartDetails = new ArrayList<>();

        @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
        private List<OrderVoucher> orderVoucherList = new ArrayList<>();

        // Shipper relationship - added for shipper system
        @ManyToOne
        @JoinColumn(name = "shipper_id")
        private Shipper shipper;

        // ==== SHIPPER TRACKING FIELDS ====

        // Payment method
        @Enumerated(EnumType.STRING)
        private PaymentMethod paymentMethod;

        // Delivery coordinates
        @Column(name = "delivery_latitude")
        private Double deliveryLatitude;

        @Column(name = "delivery_longitude")
        private Double deliveryLongitude;

        // Distance and time estimates
        private Float distance; // in meters

        @Column(name = "estimated_time")
        private Integer estimatedTime; // in minutes

        // Shipper timeline tracking
        @Column(name = "assigned_at")
        private LocalDateTime assignedAt;

        @Column(name = "accepted_at")
        private LocalDateTime acceptedAt;

        @Column(name = "picked_up_at")
        private LocalDateTime pickedUpAt;

        @Column(name = "delivered_at")
        private LocalDateTime deliveredAt;

        // Shipper earnings
        @Column(name = "shipper_earning", precision = 9, scale = 2)
        private BigDecimal shipperEarning;

        private Long tip; // customer tip for shipper

        @Column(name = "gems_earned")
        private Integer gemsEarned; // gamification points
}
