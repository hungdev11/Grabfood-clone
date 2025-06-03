package com.api.dto.response;

import com.api.dto.request.ReviewDTO;
import com.api.entity.CartDetail;
import com.api.utils.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalPrice;
    private String address;
    private OrderStatus status;
    private BigDecimal shippingFee;
    private BigDecimal discountShippingFee;
    private BigDecimal discountOrderPrice;
    private String note;
    private String payment_method;
    private List<CartDetailResponse> cartDetails;
    private boolean isReview;
    private ReviewDTO.ReviewResponse reviewResponse;
    private LocalDateTime createdAt;

    @Builder.Default
    private ShipperPickUpInfoResponse shipperPickUpInfoResponse = null;
}
