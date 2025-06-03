package com.api.repository;

import com.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository để quản lý giao dịch của shipper
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Tìm giao dịch theo shipper ID - sử dụng custom query
    @Query("SELECT t FROM Transaction t WHERE t.shipper.id = :shipperId ORDER BY t.transactionDate DESC")
    List<Transaction> findByShipperIdOrderByTransactionDateDesc(@Param("shipperId") Long shipperId);
    
    // Tìm giao dịch theo shipper ID và loại giao dịch - sử dụng custom query
    @Query("SELECT t FROM Transaction t WHERE t.shipper.id = :shipperId AND t.type = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByShipperIdAndTypeOrderByTransactionDateDesc(@Param("shipperId") Long shipperId, 
                                                                       @Param("type") Transaction.TransactionType type);
    
    // Tìm giao dịch theo shipper ID và trạng thái - sử dụng custom query
    @Query("SELECT t FROM Transaction t WHERE t.shipper.id = :shipperId AND t.status = :status ORDER BY t.transactionDate DESC")
    List<Transaction> findByShipperIdAndStatusOrderByTransactionDateDesc(@Param("shipperId") Long shipperId, 
                                                                        @Param("status") Transaction.TransactionStatus status);
    
    // Tìm giao dịch trong khoảng thời gian
    @Query("SELECT t FROM Transaction t WHERE t.shipper.id = :shipperId AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsByShipperAndDateRange(@Param("shipperId") Long shipperId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    // Tìm giao dịch theo order ID
    List<Transaction> findByOrderIdOrderByTransactionDateDesc(Long orderId);
    
    // Lấy tổng thu nhập của shipper theo loại giao dịch
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.shipper.id = :shipperId AND t.type = :type AND t.status = 'COMPLETED'")
    Long getTotalAmountByShipperAndType(@Param("shipperId") Long shipperId, 
                                       @Param("type") Transaction.TransactionType type);
    
    // Lấy thu nhập hôm nay của shipper
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.shipper.id = :shipperId AND " +
           "t.type IN ('EARNING', 'TIP', 'BONUS') AND " +
           "t.status = 'COMPLETED' AND " +
           "DATE(t.transactionDate) = CURRENT_DATE")
    Long getTodayEarningsByShipper(@Param("shipperId") Long shipperId);
    
    // Lấy thu nhập tuần này của shipper
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.shipper.id = :shipperId AND " +
           "t.type IN ('EARNING', 'TIP', 'BONUS') AND " +
           "t.status = 'COMPLETED' AND " +
           "YEARWEEK(t.transactionDate, 1) = YEARWEEK(CURDATE(), 1)")
    Long getWeekEarningsByShipper(@Param("shipperId") Long shipperId);
    
    // Lấy thu nhập tháng này của shipper
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.shipper.id = :shipperId AND " +
           "t.type IN ('EARNING', 'TIP', 'BONUS') AND " +
           "t.status = 'COMPLETED' AND " +
           "YEAR(t.transactionDate) = YEAR(CURDATE()) AND " +
           "MONTH(t.transactionDate) = MONTH(CURDATE())")
    Long getMonthEarningsByShipper(@Param("shipperId") Long shipperId);
    
    // Đếm số giao dịch theo trạng thái
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.shipper.id = :shipperId AND t.status = :status")
    Long countTransactionsByShipperAndStatus(@Param("shipperId") Long shipperId, 
                                            @Param("status") Transaction.TransactionStatus status);
    
    // Lấy giao dịch COD của shipper
    @Query("SELECT t FROM Transaction t WHERE t.shipper.id = :shipperId AND t.type = 'COD_DEPOSIT' " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> getCodTransactionsByShipper(@Param("shipperId") Long shipperId);
    
    // Lấy giao dịch pending cần xử lý
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' " +
           "ORDER BY t.transactionDate ASC")
    List<Transaction> findPendingTransactions();
    
    // Thống kê giao dịch theo loại trong khoảng thời gian
    @Query("SELECT t.type, COUNT(t), COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.type")
    List<Object[]> getTransactionStatsByType(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    // Lấy top shipper có thu nhập cao nhất
    @Query("SELECT t.shipper.id, COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.type IN ('EARNING', 'TIP', 'BONUS') AND t.status = 'COMPLETED' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.shipper.id ORDER BY SUM(t.amount) DESC")
    List<Object[]> getTopEarnersByPeriod(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    // Lấy tổng tiền COD đang giữ
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.type = 'COD_DEPOSIT' AND t.status = 'COMPLETED'")
    Long getTotalCodHolding();
    
    // Lấy giao dịch có số tiền lớn nhất
    @Query("SELECT t FROM Transaction t WHERE t.amount = " +
           "(SELECT MAX(t2.amount) FROM Transaction t2)")
    List<Transaction> getHighestAmountTransactions();
} 