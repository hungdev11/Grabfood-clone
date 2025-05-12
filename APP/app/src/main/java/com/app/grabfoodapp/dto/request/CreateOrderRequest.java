package com.app.grabfoodapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
