package com.api.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyVoucherRequest {
    private List<String> listCode;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
}
