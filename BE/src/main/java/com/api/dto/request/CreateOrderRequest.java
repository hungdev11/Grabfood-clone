package com.api.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {
    private Long cartId;
    private String address;
    private String note;
    private BigDecimal shippingFee;
    private List<String> voucherCode;
    private double lat;
    private double lon;
}
