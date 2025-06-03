package com.api.repository;

import com.api.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository để quản lý rewards của hệ thống
 */
@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    
    // Tìm reward theo type và status
    List<Reward> findByTypeAndStatus(Reward.RewardType type, Reward.RewardStatus status);
    
    // Tìm reward active
    List<Reward> findByIsActiveTrueAndStatus(Reward.RewardStatus status);
    
    // Tìm reward theo type
    List<Reward> findByType(Reward.RewardType type);
    
    // Tìm reward theo status
    List<Reward> findByStatus(Reward.RewardStatus status);
    
    // Tìm reward còn hiệu lực
    @Query("SELECT r FROM Reward r WHERE r.isActive = true AND r.status = 'ACTIVE' AND " +
           "(r.validFrom IS NULL OR r.validFrom <= :currentDate) AND " +
           "(r.validTo IS NULL OR r.validTo >= :currentDate)")
    List<Reward> findValidRewards(@Param("currentDate") LocalDate currentDate);
    
    // Tìm daily rewards
    @Query("SELECT r FROM Reward r WHERE r.type = 'DAILY' AND r.isActive = true AND r.status = 'ACTIVE'")
    List<Reward> findActiveDailyRewards();
    
    // Tìm peak hour rewards đang có hiệu lực
    @Query("SELECT r FROM Reward r WHERE r.type = 'PEAK_HOUR' AND r.isActive = true AND r.status = 'ACTIVE' AND " +
           "r.peakStartTime <= :currentTime AND r.peakEndTime >= :currentTime")
    List<Reward> findActivePeakHourRewards(@Param("currentTime") LocalTime currentTime);
    
    // Tìm achievement rewards
    @Query("SELECT r FROM Reward r WHERE r.type = 'ACHIEVEMENT' AND r.isActive = true AND r.status = 'ACTIVE'")
    List<Reward> findActiveAchievementRewards();
    
    // Tìm bonus rewards
    @Query("SELECT r FROM Reward r WHERE r.type = 'BONUS' AND r.isActive = true AND r.status = 'ACTIVE'")
    List<Reward> findActiveBonusRewards();
    
    // Tìm reward hết hạn
    @Query("SELECT r FROM Reward r WHERE r.validTo < :currentDate OR r.endDate < :currentDateTime")
    List<Reward> findExpiredRewards(@Param("currentDate") LocalDate currentDate, 
                                   @Param("currentDateTime") LocalDateTime currentDateTime);
    
    // Tìm reward theo khoảng giá trị
    @Query("SELECT r FROM Reward r WHERE r.amount BETWEEN :minAmount AND :maxAmount")
    List<Reward> findRewardsByAmountRange(@Param("minAmount") java.math.BigDecimal minAmount, 
                                         @Param("maxAmount") java.math.BigDecimal maxAmount);
    
    // Tìm reward theo gems value
    @Query("SELECT r FROM Reward r WHERE r.gemsValue >= :minGems")
    List<Reward> findRewardsByMinGems(@Param("minGems") Integer minGems);
    
    // Tìm reward có required orders
    @Query("SELECT r FROM Reward r WHERE r.requiredOrders IS NOT NULL AND r.requiredOrders > 0")
    List<Reward> findRewardsWithOrderRequirement();
    
    // Tìm reward có required rating
    @Query("SELECT r FROM Reward r WHERE r.requiredRating IS NOT NULL AND r.requiredRating > 0")
    List<Reward> findRewardsWithRatingRequirement();
    
    // Tìm reward được tạo bởi
    List<Reward> findByCreatedBy(String createdBy);
    
    // Tìm reward theo title hoặc name
    @Query("SELECT r FROM Reward r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Reward> findRewardsByKeyword(@Param("keyword") String keyword);
    
    // Đếm reward theo type
    @Query("SELECT r.type, COUNT(r) FROM Reward r GROUP BY r.type")
    List<Object[]> countRewardsByType();
    
    // Đếm reward theo status
    @Query("SELECT r.status, COUNT(r) FROM Reward r GROUP BY r.status")
    List<Object[]> countRewardsByStatus();
    
    // Tìm reward có giá trị cao nhất
    @Query("SELECT r FROM Reward r WHERE r.amount = (SELECT MAX(r2.amount) FROM Reward r2)")
    List<Reward> findHighestValueRewards();
} 