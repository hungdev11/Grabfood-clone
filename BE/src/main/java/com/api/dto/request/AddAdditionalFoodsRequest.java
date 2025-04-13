package com.api.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAdditionalFoodsRequest {
    private long restaurantId;
    private long foodId;
    private Set<Integer> additionalFoodIds;
}
