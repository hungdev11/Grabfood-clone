package com.api.repository;

import com.api.entity.Food;
import com.api.entity.FoodType;
import com.api.entity.Restaurant;
import com.api.utils.FoodKind;
import com.api.utils.FoodStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByRestaurantAndNameAndTypeAndKind(Restaurant restaurant, String name, FoodType type, FoodKind kind);
    Optional<Food> findByRestaurantAndNameAndTypeAndKind(Restaurant restaurant, String name, FoodType type, FoodKind kind);
    Page<Food> findByRestaurantAndStatus(Restaurant restaurant, FoodStatus status, Pageable pageable);
    Page<Food> findByRestaurant(Restaurant restaurant, Pageable pageable);
    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:status IS NULL OR f.status = :status)" +
            "AND (:kind IS NULL OR f.kind = :kind)")
    List<Food> findByNameContainingIgnoreCase(
            @Param("query") String query,
            @Param("status") FoodStatus status,
            @Param("kind") FoodKind kind);

    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND f.restaurant.id = :restaurantId " +
            "AND (:status IS NULL OR f.status = :status)" +
            "AND (:kind IS NULL OR f.kind = :kind)")
    List<Food> findByNameContainingIgnoreCaseAndRestaurantId(
            @Param("query") String query,
            @Param("restaurantId") Long restaurantId,
            @Param("status") FoodStatus status,
            @Param("kind") FoodKind kind);
}
