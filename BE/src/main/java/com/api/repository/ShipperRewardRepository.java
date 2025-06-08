package com.api.repository;

import com.api.entity.ShipperReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository để quản lý rewards của từng shipper
 */
@Repository
public interface ShipperRewardRepository extends JpaRepository<ShipperReward, Long> {
    
    // Tìm reward của shipper theo status - sử dụng custom query
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = :status")
    List<ShipperReward> findByShipperIdAndStatus(@Param("shipperId") Long shipperId, @Param("status") ShipperReward.RewardClaimStatus status);
    
    // Tìm tất cả reward của shipper - sử dụng custom query
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId ORDER BY sr.claimedAt DESC")
    List<ShipperReward> findByShipperIdOrderByClaimedAtDesc(@Param("shipperId") Long shipperId);
    
    // Tìm reward của shipper theo reward ID - sử dụng custom query
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.reward.id = :rewardId")
    Optional<ShipperReward> findByShipperIdAndRewardId(@Param("shipperId") Long shipperId, @Param("rewardId") Long rewardId);
    
    // Tìm reward đã claim của shipper
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'CLAIMED' " +
           "ORDER BY sr.claimedAt DESC")
    List<ShipperReward> findClaimedRewardsByShipper(@Param("shipperId") Long shipperId);
    
    // Tìm reward eligible của shipper
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'ELIGIBLE'")
    List<ShipperReward> findEligibleRewardsByShipper(@Param("shipperId") Long shipperId);
    
    // Tìm reward expired của shipper
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'EXPIRED'")
    List<ShipperReward> findExpiredRewardsByShipper(@Param("shipperId") Long shipperId);
    
    // Kiểm tra shipper đã có reward này chưa - sử dụng custom query
    @Query("SELECT COUNT(sr) > 0 FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.reward.id = :rewardId")
    boolean existsByShipperIdAndRewardId(@Param("shipperId") Long shipperId, @Param("rewardId") Long rewardId);
    
    // Đếm số reward đã claim của shipper
    @Query("SELECT COUNT(sr) FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'CLAIMED'")
    Long countClaimedRewardsByShipper(@Param("shipperId") Long shipperId);
    
    // Đếm số reward eligible của shipper
    @Query("SELECT COUNT(sr) FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND sr.status = 'ELIGIBLE'")
    Long countEligibleRewardsByShipper(@Param("shipperId") Long shipperId);
    
    // Tìm reward được claim trong khoảng thời gian
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.claimedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY sr.claimedAt DESC")
    List<ShipperReward> findRewardsClaimedBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    // Tìm reward của shipper được claim trong khoảng thời gian
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND " +
           "sr.claimedAt BETWEEN :startDate AND :endDate ORDER BY sr.claimedAt DESC")
    List<ShipperReward> findShipperRewardsClaimedBetween(@Param("shipperId") Long shipperId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);
    
    // Tìm reward theo completion percentage
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND " +
           "sr.completionPercentage >= :minPercentage")
    List<ShipperReward> findRewardsByMinCompletion(@Param("shipperId") Long shipperId,
                                                  @Param("minPercentage") Float minPercentage);
    
    // Tìm reward gần hoàn thành (>= 80%)
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND " +
           "sr.completionPercentage >= 80.0 AND sr.status = 'ELIGIBLE'")
    List<ShipperReward> findNearCompletionRewards(@Param("shipperId") Long shipperId);
    
    // Thống kê reward theo status của shipper
    @Query("SELECT sr.status, COUNT(sr) FROM ShipperReward sr WHERE sr.shipper.id = :shipperId " +
           "GROUP BY sr.status")
    List<Object[]> getRewardStatsByShipper(@Param("shipperId") Long shipperId);
    
    // Tìm top shipper có nhiều reward nhất
    @Query("SELECT sr.shipper.id, COUNT(sr) FROM ShipperReward sr WHERE sr.status = 'CLAIMED' " +
           "GROUP BY sr.shipper.id ORDER BY COUNT(sr) DESC")
    List<Object[]> getTopShippersByRewardCount();
    
    // Tìm reward phổ biến nhất (được claim nhiều nhất)
    @Query("SELECT sr.reward.id, sr.reward.name, COUNT(sr) FROM ShipperReward sr WHERE sr.status = 'CLAIMED' " +
           "GROUP BY sr.reward.id, sr.reward.name ORDER BY COUNT(sr) DESC")
    List<Object[]> getMostClaimedRewards();
    
    // Xóa reward expired cũ
    @Query("DELETE FROM ShipperReward sr WHERE sr.status = 'EXPIRED' AND sr.claimedAt < :cutoffDate")
    void deleteExpiredRewardsBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Tìm reward có notes
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.notes IS NOT NULL AND sr.notes <> ''")
    List<ShipperReward> findRewardsWithNotes();
    
    // Tìm reward của shipper theo type của reward
    @Query("SELECT sr FROM ShipperReward sr WHERE sr.shipper.id = :shipperId AND " +
           "sr.reward.type = :rewardType")
    List<ShipperReward> findShipperRewardsByType(@Param("shipperId") Long shipperId,
                                                @Param("rewardType") com.api.entity.Reward.RewardType rewardType);
} 