package com.api.repository;

import com.api.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    Optional<CartDetail> findByCartIdAndFoodId(Long cartId, Long foodId);
    boolean existsByCartIdAndFoodId(Long cartId, Long foodId);
    List<CartDetail> findByCartIdAndOrderIsNull(Long cartId);

    List<CartDetail> findByOrderId(Long orderId);
    @Query(value = "SELECT price FROM `grab-food`.food_detail WHERE end_time IS NULL AND food_id = :foodId", nativeQuery = true)
    BigDecimal findPriceByFoodId(@Param("foodId") Long foodid);

}
