package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_voucher")
public class OrderVoucher extends BaseEntity {
    @Column(nullable = false)
    private LocalDateTime timeApplied;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    private VoucherDetail voucherDetail;
}
