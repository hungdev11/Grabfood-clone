package com.api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private int quantity;
    private List<AdditionalFoodCartResponse> additionFoods;
    private BigDecimal price;
    private String note;
    private String food_img;
}
