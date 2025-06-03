package com.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_assignment",
       indexes = {
           @Index(name = "idx_order_assignment_status", columnList = "status, assigned_at")
       })
public class OrderAssignment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;
    
    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    // Enum cho trạng thái assignment
    public enum AssignmentStatus {
        ASSIGNED,    // Đã assign cho shipper
        ACCEPTED,    // Shipper đã chấp nhận
        REJECTED,    // Shipper từ chối
        EXPIRED,     // Hết thời gian phản hồi
        CANCELLED    // Bị hủy
    }
    
    @PrePersist
    public void prePersist() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
    
    // Phương thức tiện ích
    public void acceptOrder() {
        this.status = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }
    
    public void rejectOrder(String reason) {
        this.status = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }
    
    public void expireAssignment() {
        this.status = AssignmentStatus.EXPIRED;
        this.respondedAt = LocalDateTime.now();
    }
    
    public void cancelAssignment() {
        this.status = AssignmentStatus.CANCELLED;
        this.respondedAt = LocalDateTime.now();
    }
    
    public Boolean isActive() {
        return status == AssignmentStatus.ASSIGNED || status == AssignmentStatus.ACCEPTED;
    }
    
    public Boolean isPending() {
        return status == AssignmentStatus.ASSIGNED;
    }
    
    public Boolean isExpired() {
        // Kiểm tra xem assignment đã quá 60 giây mà chưa có phản hồi
        if (status == AssignmentStatus.ASSIGNED && assignedAt != null) {
            return LocalDateTime.now().isAfter(assignedAt.plusSeconds(60));
        }
        return status == AssignmentStatus.EXPIRED;
    }
    
    public Long getShipperId() {
        return shipper != null ? shipper.getId() : null;
    }
    
    public Long getOrderId() {
        return order != null ? order.getId() : null;
    }
    
    public void setEstimatedTimes(LocalDateTime pickupTime, LocalDateTime deliveryTime) {
        this.estimatedPickupTime = pickupTime;
        this.estimatedDeliveryTime = deliveryTime;
    }
} 