package com.api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperAuthResponse {
    private String token;
    private String message;
    private String phone;
    private Long shipperId;
    private String name;
    private String status;
    private Boolean isOnline;
}