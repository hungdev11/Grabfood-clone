package com.api.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateRequest {
    long userId;
    long cartDetailId;
    long foodId;
    int newQuantity;
    List<Long> additionFoodIds;
}
