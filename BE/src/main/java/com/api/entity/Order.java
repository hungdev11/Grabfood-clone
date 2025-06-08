package com.api.entity;

import com.api.utils.OrderStatus;
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
@NamedStoredProcedureQuery(
        name = "Order.getAllOrdersOfRestaurant",
        procedureName = "get_all_orders_of_restaurant",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "restaurant_id", type = Long.class)
        }
)
@Table(name = "orders") // order conflict with ORDER in mysql
public class Order extends BaseEntity{

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

    // ===============================
    // SHIPPER & DELIVERY FIELDS
    // ===============================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private Shipper shipper;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "distance_km", precision = 5, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "tip_amount", precision = 9, scale = 2)
    private BigDecimal tipAmount;

    @Column(name = "shipper_earning", precision = 9, scale = 2)
    private BigDecimal shipperEarning;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<CartDetail> cartDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderVoucher> orderVoucherList = new ArrayList<>();
    
    // ===============================
    // UTILITY METHODS
    // ===============================
    
    /**
     * Tính thời gian giao hàng (phút)
     */
    public Integer getDeliveryTimeInMinutes() {
        if (pickedUpAt != null && deliveredAt != null) {
            return (int) java.time.Duration.between(pickedUpAt, deliveredAt).toMinutes();
        }
        return null;
    }
    
    /**
     * Kiểm tra xem có shipper hay không
     */
    public boolean hasShipper() {
        return shipper != null;
    }
    
    /**
     * Lấy shipper ID
     */
    public Long getShipperId() {
        return shipper != null ? shipper.getId() : null;
    }
}
