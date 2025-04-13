package com.api.service;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.utils.FoodStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodService {
    long addFood(AddFoodRequest food);
    long adjustFoodPrice(AdjustFoodPriceRequest adjustFoodPriceRequest);
    BigDecimal getCurrentPrice(long foodId);
    BigDecimal getFoodPriceIn(long foodId, LocalDateTime time);
    GetFoodResponse getFood(long foodId, boolean isForCustomer);
    PageResponse<List<GetFoodResponse>> getFoodsOfRestaurant(long restaurantId, boolean isForCustomer, int page, int pageSize);
    void changeFoodStatus(long restaurantId, long foodId, FoodStatus foodStatus);
    PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfRestaurant(long restaurantId, boolean isForCustomer, int page, int pageSize);
    void addAdditionalFoodToFoodOfRestaurant(AddAdditionalFoodsRequest request);
    PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfFood (long restaurantId, long foodId, boolean isForCustomer, int page, int pageSize);
}
