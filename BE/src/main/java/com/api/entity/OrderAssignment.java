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
@Table(name = "order_assignment")
public class OrderAssignment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public enum AssignmentStatus {
        ASSIGNED,
        ACCEPTED,
        REJECTED,
        EXPIRED,
        CANCELLED
    }
}