package com.api.service;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.request.UpdateFoodInfoRequest;
import com.api.dto.response.*;
import com.api.entity.Restaurant;
import com.api.entity.Voucher;
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
    List<GetFoodResponse> getFoodsOfRestaurant(long restaurantId);
    void changeFoodStatus(long restaurantId, long foodId, FoodStatus foodStatus);
    PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfRestaurant(long restaurantId, boolean isForCustomer, int page, int pageSize);
    void addAdditionalFoodToFoodOfRestaurant(AddAdditionalFoodsRequest request);
    PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfFood (long restaurantId, long foodId, boolean isForCustomer, int page, int pageSize);
    void updateFoodInfo(long restaurantId, long foodId, UpdateFoodInfoRequest request);
    GetFoodGroupResponse getFoodGroupOfRestaurant(long restaurantId, boolean isForCustomer);
    List<Voucher> getValidRestaurantVouchers(Restaurant restaurant, LocalDateTime time);
    List<GetFoodResponse> searchFoods(String query, Long restaurantId, boolean isForCustomer);
    SearchResultResponse searchFoodsAndRestaurants(String query, boolean isForCustomer);
}
