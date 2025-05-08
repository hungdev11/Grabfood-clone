package com.app.grabfoodapp.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyVoucherResponse {
    private BigDecimal newOrderPrice;
    private BigDecimal newShippingFee;
    private BigDecimal discountShippingPrice;
    private BigDecimal discountOrderPrice;

}
