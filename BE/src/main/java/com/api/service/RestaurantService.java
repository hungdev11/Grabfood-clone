package com.api.service;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    long addRestaurant(AddRestaurantRequest request);
    Restaurant getRestaurant(long id);
    RestaurantResponse getRestaurantResponse(long id);
    PageResponse<List<RestaurantResponse>> getRestaurants(String sortBy, int page, int pageSize);
}
