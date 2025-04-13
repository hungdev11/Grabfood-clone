package com.api.dto.response;

import com.api.entity.CartDetail;
import com.api.utils.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private String address;
    private OrderStatus status;
    private BigDecimal shippingFee;
    private String note;
    private List<CartDetailResponse> cartDetails;
}
