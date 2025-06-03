package com.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shipper", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "phone")
       },
       indexes = {
           @Index(name = "idx_shipper_location", columnList = "current_latitude, current_longitude, is_online, status"),
           @Index(name = "idx_shipper_online_status", columnList = "status, is_online")
       })
public class Shipper extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, unique = true)
    private String phone;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(4.5);
    
    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;
    
    @Column(name = "completed_orders", nullable = false)
    private Integer completedOrders = 0;
    
    @Column(name = "acceptance_rate", nullable = false)
    private Float acceptanceRate = 100.0f;
    
    @Column(name = "cancellation_rate", nullable = false)
    private Float cancellationRate = 0.0f;
    
    @Column(name = "accepted_orders", nullable = false)
    private Integer acceptedOrders = 0;
    
    @Column(name = "rejected_orders", nullable = false)
    private Integer rejectedOrders = 0;
    
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShipperStatus status = ShipperStatus.ACTIVE;
    
    @Column(name = "vehicle_type")
    private String vehicleType;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;
    
    @Column(nullable = false)
    private Integer gems = 0;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Quan hệ với Account
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
    
    // OrderAssignment đã quản lý relationship với Orders
    // Không cần mapping trực tiếp với Order nữa
    
    // Quan hệ với OrderAssignment
    @OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderAssignment> orderAssignments = new ArrayList<>();
    
    // Quan hệ với Wallet
    @OneToOne(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;
    
    // Quan hệ với Transactions
    @OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    // Quan hệ với ShipperRewards
    @OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipperReward> shipperRewards = new ArrayList<>();
    
    // Enum cho trạng thái shipper
    public enum ShipperStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
    
    // Phương thức tiện ích để tính toán thống kê
    public Float getSuccessRate() {
        if (totalOrders == 0) return 100.0f;
        return (float) completedOrders / totalOrders * 100;
    }
    
    public Boolean isAvailableForOrder() {
        return isOnline && status == ShipperStatus.ACTIVE;
    }
    
    public void updateLocation(Double latitude, Double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.modifiedDate = LocalDateTime.now();
    }
    
    public void updateRating(BigDecimal newRating) {
        this.rating = newRating;
        this.modifiedDate = LocalDateTime.now();
    }
    
    public void incrementCompletedOrders() {
        this.completedOrders++;
        this.totalOrders++;
        updateAcceptanceRate();
    }
    
    public void incrementTotalOrders() {
        this.totalOrders++;
        updateAcceptanceRate();
    }
    
    public void incrementAcceptedOrders() {
        this.acceptedOrders++;
        updateAcceptanceRate();
    }
    
    public void incrementRejectedOrders() {
        this.rejectedOrders++;
        updateAcceptanceRate();
    }
    
    private void updateAcceptanceRate() {
        int totalAssignments = acceptedOrders + rejectedOrders;
        if (totalAssignments > 0) {
            this.acceptanceRate = (float) acceptedOrders / totalAssignments * 100;
        }
    }
    
    public void addGems(Integer gemsToAdd) {
        this.gems += gemsToAdd;
        this.modifiedDate = LocalDateTime.now();
    }
    
    public Boolean canSpendGems(Integer gemsToSpend) {
        return this.gems >= gemsToSpend;
    }
    
    public void spendGems(Integer gemsToSpend) {
        if (canSpendGems(gemsToSpend)) {
            this.gems -= gemsToSpend;
            this.modifiedDate = LocalDateTime.now();
        }
    }
} 