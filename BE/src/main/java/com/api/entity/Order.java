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

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<CartDetail> cartDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderVoucher> orderVoucherList = new ArrayList<>();
}
