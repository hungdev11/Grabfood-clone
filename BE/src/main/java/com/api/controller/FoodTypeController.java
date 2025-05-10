package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.FoodTypeResponse;
import com.api.entity.FoodType;
import com.api.service.FoodTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/food-types")
public class FoodTypeController {
    private final FoodTypeService foodTypeService;

    @PostMapping
    public ApiResponse<Long> addNewFoodType(@RequestParam String name) {
        return ApiResponse.<Long>builder()
                .code(200)
                .message("Success")
                .data(foodTypeService.addNewFoodType(name))
                .build();
    }

    @GetMapping
    public ApiResponse<List<FoodTypeResponse>> getAllFoodTypes() {
        return ApiResponse.<List<FoodTypeResponse>>builder()
                .code(200)
                .message("Success")
                .data(foodTypeService.getAllFoodTypes())
                .build();
    }
}
