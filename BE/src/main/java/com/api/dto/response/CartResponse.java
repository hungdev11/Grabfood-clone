package com.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private String restaurantName;
    private boolean isRestaurantOpen;
    private List<CartDetailResponse> listItem;
}
