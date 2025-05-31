package com.api.repository;

import com.api.dto.response.RevenueStatsDTO;
import com.api.entity.Order;
import com.api.utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrderByUserIdAndStatusOrderByIdDesc(Long userId, OrderStatus status);

    List<Order> getOrderByUserIdOrderByIdDesc(Long id);
    @Procedure(name = "Order.getAllOrdersOfRestaurant")
    List<Long> getAllOrdersOfRestaurant(@Param("restaurant_id") Long restaurantId);

    @Query(value = "CALL `grab-food`.sp_get_revenue_stats(:restaurantId, :dateFrom, :dateTo, :groupBy)", nativeQuery = true)
    List<RevenueStatsDTO> getRevenueStats(
            @Param("restaurantId") Integer restaurantId,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("groupBy") String groupBy
    );

    @Query(value = "CALL `grab-food`.sp_get_yearly_monthly_revenue(:restaurantId, :year)", nativeQuery = true)
    List<RevenueStatsDTO> getMonthlyRevenue(
            @Param("restaurantId") Integer restaurantId,
            @Param("year") Integer year
    );
    @Query("SELECT COALESCE(SUM(o.totalPrice + o.shippingFee*0.15), 0) FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
