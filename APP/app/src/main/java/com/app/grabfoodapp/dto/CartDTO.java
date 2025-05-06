package com.app.grabfoodapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CartDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddToCartRequest {
        private Long foodId;
        private int quantity;
        private List<Long> additionalItems;
        private String note;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartUpdateRequest {
        long userId;
        long cartDetailId;
        long foodId;
        int newQuantity;
        List<Long> additionFoodIds;
    }
}
