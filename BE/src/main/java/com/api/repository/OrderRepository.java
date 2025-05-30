package com.api.repository;

import com.api.dto.response.RevenueStatsDTO;
import com.api.entity.Order;
import com.api.utils.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
                        @Param("groupBy") String groupBy);

        @Query(value = "CALL `grab-food`.sp_get_yearly_monthly_revenue(:restaurantId, :year)", nativeQuery = true)
        List<RevenueStatsDTO> getMonthlyRevenue(
                        @Param("restaurantId") Integer restaurantId,
                        @Param("year") Integer year);

        // ===== SHIPPER ORDER QUERIES =====

        /**
         * Lấy đơn hàng được assign cho shipper cụ thể
         */
        @Query("SELECT o FROM Order o WHERE o.shipper.id = :shipperId ORDER BY o.orderDate DESC")
        Page<Order> findOrdersByShipperId(@Param("shipperId") Long shipperId, Pageable pageable);

        /**
         * Lấy đơn hàng được assign cho shipper với filter status
         */
        @Query("SELECT o FROM Order o WHERE o.shipper.id = :shipperId AND o.status = :status ORDER BY o.orderDate DESC")
        Page<Order> findOrdersByShipperIdAndStatus(@Param("shipperId") Long shipperId,
                        @Param("status") OrderStatus status, Pageable pageable);

        /**
         * Lấy đơn hàng available để assign (chưa có shipper và status phù hợp)
         */
        @Query("SELECT o FROM Order o WHERE o.shipper IS NULL AND o.status IN ('PROCESSING', 'READY_FOR_PICKUP') ORDER BY o.orderDate ASC")
        Page<Order> findAvailableOrdersForAssignment(Pageable pageable);

        /**
         * Tìm order cụ thể cho shipper (để verify permission)
         */
        @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.shipper.id = :shipperId")
        Optional<Order> findOrderByIdAndShipperId(@Param("orderId") Long orderId, @Param("shipperId") Long shipperId);

        /**
         * Lấy orders gần vị trí shipper hiện tại
         */
        @Query(value = """
                        SELECT o.* FROM orders o
                        INNER JOIN user u ON o.user_id = u.id
                        WHERE o.shipper_id IS NULL
                        AND o.status IN ('PROCESSING', 'READY_FOR_PICKUP')
                        AND o.delivery_latitude IS NOT NULL
                        AND o.delivery_longitude IS NOT NULL
                        AND (
                            6371 * ACOS(
                                COS(RADIANS(:lat)) * COS(RADIANS(o.delivery_latitude)) *
                                COS(RADIANS(o.delivery_longitude) - RADIANS(:lon)) +
                                SIN(RADIANS(:lat)) * SIN(RADIANS(o.delivery_latitude))
                            )
                        ) < :radiusKm
                        """, nativeQuery = true)
        Page<Order> findNearbyAvailableOrders(
                        @Param("lat") double lat,
                        @Param("lon") double lon,
                        @Param("radiusKm") double radiusKm,
                        Pageable pageable);

        /**
         * Lấy tất cả orders của một shipper cụ thể
         */
        @Query("SELECT o FROM Order o WHERE o.shipper.id = :shipperId ORDER BY o.orderDate DESC")
        List<Order> findAllByShipperId(@Param("shipperId") Long shipperId);
}
