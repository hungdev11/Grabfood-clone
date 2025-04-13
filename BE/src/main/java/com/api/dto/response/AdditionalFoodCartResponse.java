package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalFoodCartResponse {
    private long id;
    private String name;
    private BigDecimal price;
}
