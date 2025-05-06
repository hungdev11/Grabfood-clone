package com.app.grabfoodapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private long cartId;
    private Long restaurantId;
    private String restaurantName;
    private boolean isRestaurantOpen;
    private List<CartDetailDTO> listItem;
}
