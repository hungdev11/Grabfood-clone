package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverLoginResponse {
    
    private String token;
    
    private String message;
    
    private Long shipperId;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    private BigDecimal rating;
    
    private String status;
    
    private Boolean isOnline;
    
    private String vehicleType;
    
    private String licensePlate;
    
    private Integer totalOrders;
    
    private Integer completedOrders;
    
    private Float acceptanceRate;
    
    private Integer gems;
    
    private Double currentLatitude;
    
    private Double currentLongitude;
} 