package com.api.controller;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Restaurant;
import com.api.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ApiResponse<Long> addNewRestaurant(@RequestBody AddRestaurantRequest newRestaurant) {
        return ApiResponse.<Long>builder()
                .code(200)
                .message("Success")
                .data(restaurantService.addRestaurant(newRestaurant))
                .build();
    }

    @GetMapping("/{restaurant_id}")
    public ApiResponse<RestaurantResponse> getRestaurantById(@PathVariable long restaurant_id) {
        return ApiResponse.<RestaurantResponse>builder()
                .code(200)
                .message("Success")
                .data(restaurantService.getRestaurantResponse(restaurant_id))
                .build();
    }

    @GetMapping()
    public ApiResponse<?> getFoodsOfRestaurant(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.builder()
                .data(restaurantService.getRestaurants(sortBy, page, pageSize))
                .message("Success")
                .code(200)
                .build();
    }

    @Value("${radiusKm}")
    private double radiusKm;

    @GetMapping("/nearby")
    public ApiResponse<?> getRestaurantsNearBy(@RequestParam double userLat, @RequestParam double userLon) {
        return ApiResponse.builder()
                .data(restaurantService.getNearbyRestaurants(userLat, userLon, radiusKm))
                .message("Success")
                .code(200)
                .build();
    }
}
