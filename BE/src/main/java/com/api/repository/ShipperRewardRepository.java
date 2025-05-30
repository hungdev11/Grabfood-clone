package com.api.repository;

import com.api.entity.ShipperReward;
import com.api.utils.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShipperRewardRepository extends JpaRepository<ShipperReward, Long> {

    /**
     * Lấy tất cả shipper rewards của một shipper
     */
    List<ShipperReward> findByShipperId(Long shipperId);

    /**
     * Lấy shipper reward cụ thể
     */
    Optional<ShipperReward> findByShipperIdAndRewardId(Long shipperId, Long rewardId);

    /**
     * Lấy rewards theo status
     */
    List<ShipperReward> findByShipperIdAndStatus(Long shipperId, ClaimStatus status);

    /**
     * Đếm số rewards đã claimed
     */
    @Query("SELECT COUNT(sr) FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'CLAIMED'")
    Integer countClaimedRewards(@Param("shipperId") Long shipperId);

    /**
     * Đếm số rewards eligible
     */
    @Query("SELECT COUNT(sr) FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'ELIGIBLE'")
    Integer countEligibleRewards(@Param("shipperId") Long shipperId);
}