package com.api.dto.request;

import com.api.utils.FoodKind;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFoodRequest {
    private String name;
    private String image;
    private String description;
    private FoodKind kind;
    private String type;
    private BigDecimal price;
    private long restaurantId;
}
