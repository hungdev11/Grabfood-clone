package com.app.grabfoodapp.apiservice.restaurant;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.RestaurantDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantService {
    @GET("restaurants")
    Call<ApiResponse<List<RestaurantDTO.RestaurantResponse>>> getRestaurants(
            @Query("sortBy") String sortBy,
            @Query("userLat") double userLat,
            @Query("userLon") double userLon
    );
}
