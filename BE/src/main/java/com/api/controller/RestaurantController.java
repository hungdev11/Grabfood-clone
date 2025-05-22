package com.api.controller;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.UpdateRestaurantRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Restaurant;
import com.api.service.RestaurantService;
import com.api.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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



    @GetMapping("/{restaurantId}")
    public ApiResponse<RestaurantResponse> getRestaurantById(
            @PathVariable long restaurantId,
            @RequestParam(defaultValue = "-1") double userLat,
            @RequestParam(defaultValue = "-1") double userLon) {
        return ApiResponse.<RestaurantResponse>builder()
                .code(200)
                .message("Success")
                .data(restaurantService.getRestaurantResponse(restaurantId, userLat, userLon))
                .build();
    }

    @GetMapping()
    public ApiResponse<?> getRestaurants(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "-1") double userLat,
            @RequestParam(defaultValue = "-1") double userLon) {
        return ApiResponse.builder()
                .data(restaurantService.getRestaurants(sortBy, userLat, userLon))
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

    @PutMapping("/{restaurantId}/handle-order/{orderId}")
    public ApiResponse<?> handleOrder(@PathVariable long restaurantId, @PathVariable Long orderId, @RequestParam OrderStatus status) {
        restaurantService.handleOrder(restaurantId, orderId, status);
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .build();
    }

    @PutMapping("/{restaurantId}")
    public ApiResponse<RestaurantResponse> updateRestaurantInfo(@PathVariable long restaurantId, @RequestBody UpdateRestaurantRequest request) {
        restaurantService.updateRestaurantInfo(restaurantId, request);
        return ApiResponse.<RestaurantResponse>builder()
                .code(200)
                .message("Success")
                .build();
    }
    @PutMapping("/{restaurantId}/approve")
    public ApiResponse<Void> approveRestaurant(@PathVariable long restaurantId) {
        restaurantService.approveRestaurant(restaurantId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Restaurant approved successfully")
                .build();
    }

    @PutMapping("/{restaurantId}/reject")
    public ApiResponse<Void> rejectRestaurant(@PathVariable long restaurantId) {
        restaurantService.rejectRestaurant(restaurantId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Restaurant rejected successfully")
                .build();
    }
    @PutMapping("/{restaurantId}/inactive")
    public ApiResponse<Void> setRestaurantInactive(@PathVariable long restaurantId) {
        restaurantService.setRestaurantStatus(restaurantId, "INACTIVE");
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Restaurant set to inactive successfully")
                .build();
    }

    @PutMapping("/{restaurantId}/active")
    public ApiResponse<Void> setRestaurantActive(@PathVariable long restaurantId) {
        restaurantService.setRestaurantStatus(restaurantId, "ACTIVE");
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Restaurant set to active successfully")
                .build();
    }

    @GetMapping("/pending")
    public ApiResponse<List<RestaurantResponse>> getPendingRestaurants() {
        List<RestaurantResponse> pendingRestaurants = restaurantService.getPendingRestaurants();
        return ApiResponse.<List<RestaurantResponse>>builder()
                .code(200)
                .message("Success")
                .data(pendingRestaurants)
                .build();
    }
    @GetMapping("/all")
    public ApiResponse<List<RestaurantResponse>> getAllRestaurants() {
        return ApiResponse.<List<RestaurantResponse>>builder()
                .code(200)
                .message("Success")
                .data(restaurantService.getAllRestaurants())
                .build();
    }
    @GetMapping("/username/{username}")
    public ApiResponse<Long> getRestaurantByUsername(@PathVariable String username) {
        Long restaurantId = restaurantService.getRestaurantByUsername(username);
        return ApiResponse.<Long>builder()
                .code(200)
                .message("Success")
                .data(restaurantId)
                .build();
    }
}
