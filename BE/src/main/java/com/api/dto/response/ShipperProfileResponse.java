package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperProfileResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Boolean isOnline;
    private String status;
    private BigDecimal rating;
    private Integer totalOrders;
    private Integer completedOrders;
    private Float acceptanceRate;
    private Float cancellationRate;
    private Long totalEarnings;
    private Integer gems;
    private String vehicleType;
    private String vehicleNumber;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime createdDate;
}