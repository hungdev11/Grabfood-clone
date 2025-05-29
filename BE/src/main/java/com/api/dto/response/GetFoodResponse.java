package com.api.dto.response;

import com.api.utils.FoodStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFoodResponse {
    private long id;
    private String name;
    private String image;
    private String description;
    private FoodStatus status;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal rating;
    private String type;
    private String kind;
    private Long restaurantId;
}
