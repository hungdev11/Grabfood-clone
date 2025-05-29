package com.api.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipperProfileRequest {
    private String name;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private String licensePlate;
}