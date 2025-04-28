package com.app.grabfoodapp.apiservice.food;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoodService {
    @GET("foods/restaurant/{restaurantId}")
    Call<ApiResponse<FoodDTO.GetFoodGroupResponse>> getFoodRestaurantHome(
            @Path("restaurantId") long restaurantId  // Sử dụng @Path để thay thế {restaurantId} trong URL
    );

    @GET("foods/additional/{foodId}")
    Call<PageResponse<List<FoodDTO.GetFoodResponse>>> getAdditionalFoodsOfFood(
            @Query("restaurantId") long restaurantId,
            @Path("foodId") long foodId,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("isForCustomers") boolean isForCustomer
    );

}
