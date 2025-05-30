package com.app.grabfoodapp.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartUpdateRequest {
    long userId;
    long cartDetailId;
    long foodId;
    int newQuantity;
    List<Long> additionFoodIds;
}
