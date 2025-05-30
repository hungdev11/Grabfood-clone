package com.api.repository;

import com.api.entity.Reward;
import com.api.utils.RewardStatus;
import com.api.utils.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    /**
     * Lấy rewards đang active và chưa hết hạn
     */
    @Query("SELECT r FROM Reward r WHERE r.status = :status AND (r.endDate IS NULL OR r.endDate > :now)")
    List<Reward> findActiveRewards(@Param("status") RewardStatus status, @Param("now") LocalDateTime now);

    /**
     * Lấy rewards theo type và status
     */
    List<Reward> findByTypeAndStatus(RewardType type, RewardStatus status);

    /**
     * Lấy rewards trong khoảng thời gian
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' AND " +
            "(r.startDate IS NULL OR r.startDate <= :now) AND " +
            "(r.endDate IS NULL OR r.endDate > :now)")
    List<Reward> findValidRewards(@Param("now") LocalDateTime now);
}