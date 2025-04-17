package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_info")
public class PaymentInfo extends BaseEntity{
    private String paymentName;

    @Column(name = "PAYMENT_CODE")
    private String paymentCode;

    private LocalDateTime create_at;

    @Column(name = "PAYMENTAMOUNT")
    private BigDecimal paymentAmount;

    @Column(name = "PAYMENT_STATUS")
    private String status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
