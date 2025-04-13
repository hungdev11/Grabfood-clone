package com.api.repository;

import com.api.entity.Food;
import com.api.entity.FoodMainAndAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodMainAndAdditionalRepository extends JpaRepository<FoodMainAndAddition, Long> {
    boolean existsByMainFoodAndAdditionFood(Food main, Food additional);
}
