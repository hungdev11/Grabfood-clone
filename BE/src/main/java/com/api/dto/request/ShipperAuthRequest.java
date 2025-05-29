package com.api.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipperAuthRequest {
    private String phone; // Use phone as username for shipper
    private String password;
}