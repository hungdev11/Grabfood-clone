package com.api.repository;

import com.api.entity.Shipper;
import com.api.utils.ShipperStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {

    Optional<Shipper> findByAccountId(Long accountId);

    List<Shipper> findByStatusAndIsOnline(ShipperStatus status, Boolean isOnline);

    @Query(value = """
            SELECT s.* FROM shipper s
            WHERE s.status = 'ACTIVE'
            AND s.is_online = true
            AND s.current_latitude IS NOT NULL
            AND s.current_longitude IS NOT NULL
            AND (
                6371 * ACOS(
                    COS(RADIANS(:lat)) * COS(RADIANS(s.current_latitude)) *
                    COS(RADIANS(s.current_longitude) - RADIANS(:lon)) +
                    SIN(RADIANS(:lat)) * SIN(RADIANS(s.current_latitude))
                )
            ) < :radiusKm
            """, nativeQuery = true)
    List<Shipper> findNearbyShippers(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusKm") double radiusKm);

    Optional<Shipper> findByPhone(String phone);
}