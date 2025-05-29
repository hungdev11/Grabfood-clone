package com.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultResponse {
    private List<GetFoodResponse> foods;
    private List<RestaurantResponse> restaurants;
}
