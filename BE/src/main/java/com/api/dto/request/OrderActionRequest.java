package com.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderActionRequest {
    
    @NotNull(message = "ID đơn hàng không được để trống")
    private Long orderId;
    
    private String rejectionReason;
    
    private Double estimatedPickupTime; // Thời gian dự kiến lấy hàng (phút)
    
    private Double estimatedDeliveryTime; // Thời gian dự kiến giao hàng (phút)
    
    private String note;
} 