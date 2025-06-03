package com.api.repository;

import com.api.entity.OrderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment, Long> {

       // Tìm assignment theo order ID - sử dụng custom query
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.order.id = :orderId")
       List<OrderAssignment> findByOrderId(@Param("orderId") Long orderId);

       // Tìm assignment theo shipper ID - sử dụng custom query
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId")
       List<OrderAssignment> findByShipperId(@Param("shipperId") Long shipperId);

       // Tìm assignment theo order và shipper - sử dụng custom query
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.order.id = :orderId AND oa.shipper.id = :shipperId")
       Optional<OrderAssignment> findByOrderIdAndShipperId(@Param("orderId") Long orderId,
                     @Param("shipperId") Long shipperId);

       // Tìm assignment active cho order
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.order.id = :orderId AND oa.status IN ('ASSIGNED', 'ACCEPTED')")
       List<OrderAssignment> findActiveAssignmentsByOrderId(@Param("orderId") Long orderId);

       // Tìm assignment pending cho shipper
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId AND oa.status = 'ASSIGNED'")
       List<OrderAssignment> findPendingAssignmentsByShipperId(@Param("shipperId") Long shipperId);

       // Tìm assignment đã accept cho shipper
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId AND oa.status = 'ACCEPTED'")
       List<OrderAssignment> findAcceptedAssignmentsByShipperId(@Param("shipperId") Long shipperId);

       // Tìm assignment theo status
       List<OrderAssignment> findByStatus(OrderAssignment.AssignmentStatus status);

       // Tìm assignment theo status và shipper - sử dụng custom query
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId AND oa.status = :status")
       List<OrderAssignment> findByShipperIdAndStatus(@Param("shipperId") Long shipperId,
                     @Param("status") OrderAssignment.AssignmentStatus status);

       // ==================== METHODS MỚI CHO SMART ASSIGNMENT ====================

       // Tìm assignments theo order và status
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.order.id = :orderId AND oa.status = :status")
       List<OrderAssignment> findByOrderIdAndStatus(@Param("orderId") Long orderId,
                     @Param("status") OrderAssignment.AssignmentStatus status);

       // Kiểm tra shipper có assignment với status trong list không
       @Query("SELECT COUNT(oa) > 0 FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId AND oa.status IN :statuses")
       boolean existsByShipperIdAndStatusIn(@Param("shipperId") Long shipperId,
                     @Param("statuses") List<OrderAssignment.AssignmentStatus> statuses);

       // Kiểm tra shipper có trong cooldown period không (đã reject order này trong
       // thời gian gần đây)
       @Query("SELECT COUNT(oa) > 0 FROM OrderAssignment oa WHERE " +
                     "oa.order.id = :orderId AND oa.shipper.id = :shipperId AND oa.status = :status AND oa.respondedAt > :afterTime")
       boolean existsByOrderIdAndShipperIdAndStatusAndRespondedAtAfter(@Param("orderId") Long orderId,
                     @Param("shipperId") Long shipperId,
                     @Param("status") OrderAssignment.AssignmentStatus status,
                     @Param("afterTime") LocalDateTime afterTime);

       // Tìm assignment expired (quá 60 giây mà chưa phản hồi)
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.status = 'ASSIGNED' AND oa.assignedAt < :expiredTime")
       List<OrderAssignment> findExpiredAssignments(@Param("expiredTime") LocalDateTime expiredTime);

       // Tìm assignment trong khoảng thời gian
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.assignedAt BETWEEN :startTime AND :endTime")
       List<OrderAssignment> findAssignmentsBetween(@Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime);

       // Tìm assignment theo shipper và thời gian
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND oa.assignedAt BETWEEN :startTime AND :endTime")
       List<OrderAssignment> findAssignmentsByShipperAndTimeBetween(@Param("shipperId") Long shipperId,
                     @Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime);

       // Đếm số assignment theo status của shipper
       @Query("SELECT COUNT(oa) FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND oa.status = :status")
       Long countAssignmentsByShipperAndStatus(@Param("shipperId") Long shipperId,
                     @Param("status") OrderAssignment.AssignmentStatus status);

       // Tìm assignment mới nhất của shipper
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.shipper.id = :shipperId " +
                     "ORDER BY oa.assignedAt DESC")
       List<OrderAssignment> findLatestAssignmentsByShipperId(@Param("shipperId") Long shipperId);

       // Tìm assignment mới nhất cho order
       @Query("SELECT oa FROM OrderAssignment oa WHERE oa.order.id = :orderId " +
                     "ORDER BY oa.assignedAt DESC")
       List<OrderAssignment> findLatestAssignmentsByOrderId(@Param("orderId") Long orderId);

       // Kiểm tra xem order đã được assign cho shipper chưa
       @Query("SELECT COUNT(oa) > 0 FROM OrderAssignment oa WHERE " +
                     "oa.order.id = :orderId AND oa.shipper.id = :shipperId")
       boolean existsByOrderIdAndShipperId(@Param("orderId") Long orderId,
                     @Param("shipperId") Long shipperId);

       // Tìm assignment chưa phản hồi của shipper
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND oa.status = 'ASSIGNED' AND oa.respondedAt IS NULL")
       List<OrderAssignment> findUnrespondedAssignmentsByShipperId(@Param("shipperId") Long shipperId);

       // Thống kê tỷ lệ accept/reject của shipper
       @Query("SELECT oa.status, COUNT(oa) FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND oa.status IN ('ACCEPTED', 'REJECTED') " +
                     "GROUP BY oa.status")
       List<Object[]> getShipperResponseStats(@Param("shipperId") Long shipperId);

       // Tìm assignment theo khoảng thời gian phản hồi
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.respondedAt BETWEEN :startTime AND :endTime")
       List<OrderAssignment> findAssignmentsByResponseTimeBetween(@Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime);

       // Lấy thời gian phản hồi trung bình của shipper
       @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, oa.assignedAt, oa.respondedAt)) FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND oa.respondedAt IS NOT NULL")
       Double getAverageResponseTimeByShipperId(@Param("shipperId") Long shipperId);

       // Tìm assignment có rejection reason
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.status = 'REJECTED' AND oa.rejectionReason IS NOT NULL")
       List<OrderAssignment> findRejectedAssignmentsWithReason();

       // Tìm assignment của shipper theo ngày
       @Query("SELECT oa FROM OrderAssignment oa WHERE " +
                     "oa.shipper.id = :shipperId AND DATE(oa.assignedAt) = DATE(:date)")
       List<OrderAssignment> findAssignmentsByShipperAndDate(@Param("shipperId") Long shipperId,
                     @Param("date") LocalDateTime date);
}