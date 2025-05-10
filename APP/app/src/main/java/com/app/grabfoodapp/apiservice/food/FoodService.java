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
            @Path("restaurantId") long restaurantId,
            @Query("isForCustomer") boolean isForCustomer
    );

    @GET("foods/additional/{foodId}")
    Call<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>> getAdditionalFoodsOfFood(
            @Path("foodId") long foodId,
            @Query("restaurantId") long restaurantId,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("isForCustomers") boolean isForCustomer
    );
}
