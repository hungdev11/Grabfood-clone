package com.api.controller;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.GetFoodResponse;
import com.api.service.FoodService;
import com.api.utils.FoodStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ApiResponse<Long> addNewFood(@RequestBody AddFoodRequest newFood) {
        return ApiResponse.<Long>builder()
                .code(202)
                .message("Success")
                .data(foodService.addFood(newFood))
                .build();
    }

    @PostMapping("/adjust-price")
    public ApiResponse<Long> adjustFoodPrice(@RequestBody AdjustFoodPriceRequest request) {
        return ApiResponse.<Long>builder()
                .code(202)
                .message("Success")
                .data(foodService.adjustFoodPrice(request))
                .build();
    }

    @GetMapping("/{foodId}")
    public ApiResponse<GetFoodResponse> getFood(@PathVariable long foodId, @RequestParam(defaultValue = "false") boolean isForCustomer) {
        return ApiResponse.<GetFoodResponse>builder()
                .code(200)
                .message("Success")
                .data(foodService.getFood(foodId, isForCustomer))
                .build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ApiResponse<?> getFoodsOfRestaurant(
            @PathVariable long restaurantId,
            @RequestParam(defaultValue = "false") boolean isForCustomer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.builder()
                .data(foodService.getFoodsOfRestaurant(restaurantId, isForCustomer, page, pageSize))
                .message("Success")
                .code(200)
                .build();
    }

    @PutMapping("/{foodId}")
    public ApiResponse<?> updateFoodStatus(@PathVariable long foodId, @RequestParam long restaurantId, @RequestParam FoodStatus foodStatus) {
        foodService.changeFoodStatus(restaurantId, foodId, foodStatus);
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .build();
    }

    @GetMapping("/additional")
    public ApiResponse<?> getAdditionalFoodsOfRestaurant(
            @RequestParam long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "false") boolean isForCustomer
    ) {
        return ApiResponse.builder()
                .data(foodService.getAdditionalFoodsOfRestaurant(restaurantId, isForCustomer, page, pageSize))
                .message("Success")
                .code(200)
                .build();
    }

    @GetMapping("/additional/{foodId}")
    public ApiResponse<?> getAdditionalFoodsOfFood(
            @RequestParam long restaurantId,
            @PathVariable long foodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "false") boolean isForCustomer
    ) {
        return ApiResponse.builder()
                .data(foodService.getAdditionalFoodsOfFood(restaurantId, foodId, isForCustomer, page, pageSize))
                .message("Success")
                .code(200)
                .build();
    }

    @PostMapping("/additional")
    public ApiResponse<?> addAdditionalFoodsToFood(@RequestBody AddAdditionalFoodsRequest additionalFoodsRequest) {
        foodService.addAdditionalFoodToFoodOfRestaurant(additionalFoodsRequest);
        return ApiResponse.builder()
                .message("Success")
                .code(200)
                .build();
    }

}
