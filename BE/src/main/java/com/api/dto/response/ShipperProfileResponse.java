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
    private Integer gems;
    private String vehicleType;
    private String vehicleNumber;
    private String licensePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime createdDate;

    // Wallet information (có thể được thêm riêng nếu cần)
    private Long totalEarnings; // Sẽ được lấy từ wallet
    private Long currentBalance;
    private Long todayEarnings;
}