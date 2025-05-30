package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column
    private String description;

    @Column(name = "order_id")
    private Long orderId;

    @Column
    private Long commission;

    @Column(name = "delivery_fee")
    private Long deliveryFee;

    @Column
    private Long tip;

    @Column(name = "net_amount")
    private Long netAmount;

    public enum TransactionType {
        EARNING,
        TIP,
        COMMISSION,
        COD_DEPOSIT,
        BONUS,
        TOP_UP
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}