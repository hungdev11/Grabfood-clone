package com.api.repository;

import com.api.entity.Account;
import com.api.entity.Restaurant;
import com.api.utils.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = """
    SELECT r.* FROM restaurant r
    JOIN address a ON r.address_id = a.id
    WHERE a.latitude BETWEEN :minLat AND :maxLat
      AND a.longitude BETWEEN :minLon AND :maxLon
      AND (
        6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(a.latitude)) *
            COS(RADIANS(a.longitude) - RADIANS(:lon)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(a.latitude))
        )
      ) < :radius
""", nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radius") double radiusKm,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon
    );
    List<Restaurant> findAllByStatus(RestaurantStatus status);
    Optional<Restaurant> findByAccount(Account account);
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> findByNameContainingIgnoreCase(@Param("query") String query);

    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) AND r.status = :status")
    List<Restaurant> findByNameContainingIgnoreCaseAndStatus(@Param("query") String query, @Param("status") RestaurantStatus status);
}