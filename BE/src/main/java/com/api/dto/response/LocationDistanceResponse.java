package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDistanceResponse {
    private Double distance;
    private Double duration;
    private BigDecimal shippingFee;
}
