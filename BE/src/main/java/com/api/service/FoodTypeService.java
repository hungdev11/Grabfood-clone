package com.api.service;

import com.api.entity.FoodType;

public interface FoodTypeService {
    long addNewFoodType(String name);
    FoodType getFoodTypeByName(String name);
}
