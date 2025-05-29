package com.app.grabfoodapp.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckDistanceResponse {
    private boolean check;
    private Double distance;
    private Double duration;
    private BigDecimal shippingFee;
}
