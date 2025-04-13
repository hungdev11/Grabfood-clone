package com.api.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustFoodPriceRequest {
    private long restaurantId;
    private long foodId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}
