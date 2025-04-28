package com.app.grabfoodapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FoodDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetFoodGroupResponse {
        List<String> types;
        List<GetFoodResponse> foods;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class GetFoodResponse implements Serializable {
        private long id;
        private String name;
        private String image;
        private String description;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private BigDecimal rating;
        private String type;
    }
}
