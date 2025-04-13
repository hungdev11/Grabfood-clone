package com.api.repository;

import com.api.entity.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {
    boolean existsByName(String name);
    Optional<FoodType> findByName(String name);
}
