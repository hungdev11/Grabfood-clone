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
@Table(name = "wallet")
public class Wallet extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "shipper_id", nullable = false, unique = true)
    private Shipper shipper;

    @Column(name = "current_balance", nullable = false)
    @Builder.Default
    private Long currentBalance = 0L;

    @Column(name = "cod_holding", nullable = false)
    @Builder.Default
    private Long codHolding = 0L;

    @Column(name = "total_earnings", nullable = false)
    @Builder.Default
    private Long totalEarnings = 0L;

    @Column(name = "today_earnings", nullable = false)
    @Builder.Default
    private Long todayEarnings = 0L;

    @Column(name = "week_earnings", nullable = false)
    @Builder.Default
    private Long weekEarnings = 0L;

    @Column(name = "month_earnings", nullable = false)
    @Builder.Default
    private Long monthEarnings = 0L;

    @Column(name = "is_eligible_for_cod", nullable = false)
    @Builder.Default
    private Boolean isEligibleForCod = false;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}