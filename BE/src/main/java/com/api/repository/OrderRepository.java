package com.api.repository;

import com.api.dto.response.RevenueStatsDTO;
import com.api.entity.Order;
import com.api.utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrderByUserIdAndStatus(Long user_id, OrderStatus status);

    List<Order> getOrderByUserId(Long user_id);
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
}
