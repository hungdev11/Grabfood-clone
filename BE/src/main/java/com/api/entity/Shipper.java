package com.api.entity;

import com.api.utils.ShipperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipper", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "email")
})
public class Shipper extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_online", nullable = false)
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShipperStatus status = ShipperStatus.ACTIVE;

    @Column(nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = new BigDecimal("5.00");

    @Column(name = "total_orders", nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "completed_orders", nullable = false)
    @Builder.Default
    private Integer completedOrders = 0;

    @Column(name = "acceptance_rate", nullable = false)
    @Builder.Default
    private Float acceptanceRate = 100.0f;

    @Column(name = "cancellation_rate", nullable = false)
    @Builder.Default
    private Float cancellationRate = 0.0f;

    @Column(name = "gems", nullable = false)
    @Builder.Default
    private Integer gems = 0;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    // Quan hệ với Wallet - tất cả thông tin tài chính được quản lý ở đây
    @OneToOne(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

    // Business methods
    public BigDecimal getAcceptanceRateDecimal() {
        if (totalOrders == 0)
            return new BigDecimal("100.00");
        return new BigDecimal(completedOrders)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalOrders), 2, java.math.RoundingMode.HALF_UP);
    }

    public boolean isAvailableForOrder() {
        return isOnline && status == ShipperStatus.ACTIVE;
    }

    // Tiện ích để lấy tổng thu nhập từ wallet
    public Long getTotalEarnings() {
        return wallet != null ? wallet.getTotalEarnings() : 0L;
    }
}