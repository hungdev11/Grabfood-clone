package com.app.grabfoodapp.dto.response;

import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.RestaurantDTO;

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
public class SearchResultResponse {
    private List<FoodDTO.GetFoodResponse> foods;
    private List<RestaurantDTO.RestaurantResponse> restaurants;
}
