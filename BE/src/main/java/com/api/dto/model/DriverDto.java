package com.api.dto.model;

import com.api.entity.Shipper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Double currentLatitude;
    private Double currentLongitude;
    private BigDecimal rating;
    private Integer totalOrders;
    private Integer completedOrders;
    private Float acceptanceRate;
    private Float cancellationRate;
    private Boolean isOnline;
    private String status;
    private String vehicleType;
    private String vehicleNumber;
    private String licensePlate;

    public static DriverDto fromEntity(Shipper shipper) {
        return DriverDto.builder()
                .id(shipper.getId())
                .name(shipper.getName())
                .email(shipper.getEmail())
                .phone(shipper.getPhone())
                .currentLatitude(shipper.getCurrentLatitude())
                .currentLongitude(shipper.getCurrentLongitude())
                .rating(shipper.getRating())
                .totalOrders(shipper.getTotalOrders())
                .completedOrders(shipper.getCompletedOrders())
                .acceptanceRate(shipper.getAcceptanceRate())
                .cancellationRate(shipper.getCancellationRate())
                .isOnline(shipper.getIsOnline())
                .status(shipper.getStatus().name())
                .vehicleType(shipper.getVehicleType())
                .vehicleNumber(shipper.getVehicleNumber())
                .licensePlate(shipper.getLicensePlate())
                .build();
    }
}
