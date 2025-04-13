package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyVoucherResponse {
    private BigDecimal newTotalPrice;
    private BigDecimal discountPrice;
}
