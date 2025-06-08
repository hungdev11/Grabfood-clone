package com.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipperPickUpInfoResponse {
    private String name;
    private String phoneNumber;
    private String vehicleType;
    private String vehicleNumber;
    private String paymentMethod;
}
