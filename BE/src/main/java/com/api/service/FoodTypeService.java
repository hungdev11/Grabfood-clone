package com.api.service;

import com.api.dto.response.FoodTypeResponse;
import com.api.entity.FoodType;

import java.util.List;

public interface FoodTypeService {
    long addNewFoodType(String name);
    FoodType getFoodTypeByName(String name);
    List<FoodTypeResponse> getAllFoodTypes();
}
