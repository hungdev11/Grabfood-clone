package com.api.service;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.UpdateRestaurantRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Restaurant;
import com.api.utils.OrderStatus;

import java.util.List;

public interface RestaurantService {
    long addRestaurant(AddRestaurantRequest request);

    Restaurant getRestaurant(long id);

    RestaurantResponse getRestaurantResponse(long id, double userLat, double userLon);

    List<RestaurantResponse> getRestaurants(String sortBy, double userLat, double userLon);

    List<RestaurantResponse> getNearbyRestaurants(double lat, double lon, double radiusKm);

    PageResponse<List<RestaurantResponse>> getRestaurantsForAdmin(String sortBy, int page, int pageSize);

    void handleOrder(long restaurantId, long orderId, OrderStatus status);

    void updateRestaurantInfo(long restaurantId, UpdateRestaurantRequest request);
}