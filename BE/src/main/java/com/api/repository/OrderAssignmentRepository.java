package com.api.repository;

import com.api.entity.OrderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment, Long> {

    /**
     * Tìm assignment theo order ID và shipper ID
     */
    Optional<OrderAssignment> findByOrderIdAndShipperId(Long orderId, Long shipperId);

    /**
     * Tìm tất cả assignments của một shipper
     */
    List<OrderAssignment> findByShipperIdOrderByAssignedAtDesc(Long shipperId);

    /**
     * Tìm tất cả assignments của một order
     */
    List<OrderAssignment> findByOrderIdOrderByAssignedAtDesc(Long orderId);

    /**
     * Tìm assignments theo status
     */
    List<OrderAssignment> findByStatus(OrderAssignment.AssignmentStatus status);

    /**
     * Tìm assignments của shipper theo status
     */
    List<OrderAssignment> findByShipperIdAndStatus(Long shipperId, OrderAssignment.AssignmentStatus status);

    /**
     * Đếm số assignments theo shipper và status
     */
    @Query("SELECT COUNT(oa) FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId AND oa.status = :status")
    Long countByShipperIdAndStatus(@Param("shipperId") Long shipperId,
            @Param("status") OrderAssignment.AssignmentStatus status);
}