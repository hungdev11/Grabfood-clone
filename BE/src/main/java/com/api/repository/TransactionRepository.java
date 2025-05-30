package com.api.repository;

import com.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        /**
         * Tìm tất cả transactions của một shipper
         */
        List<Transaction> findByShipperIdOrderByTransactionDateDesc(Long shipperId);

        /**
         * Tìm transactions theo shipper và type
         */
        List<Transaction> findByShipperIdAndType(Long shipperId, Transaction.TransactionType type);

        /**
         * Tìm transactions theo shipper và status
         */
        List<Transaction> findByShipperIdAndStatus(Long shipperId, Transaction.TransactionStatus status);

        /**
         * Tìm transactions trong khoảng thời gian
         */
        List<Transaction> findByShipperIdAndTransactionDateBetween(
                        Long shipperId, LocalDateTime startDate, LocalDateTime endDate);

        /**
         * Tính tổng thu nhập của shipper theo type
         */
        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.shipper.id = :shipperId AND t.type = :type AND t.status = 'COMPLETED'")
        Long sumAmountByShipperIdAndType(@Param("shipperId") Long shipperId,
                        @Param("type") Transaction.TransactionType type);

        /**
         * Tính tổng thu nhập trong ngày hôm nay
         */
        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.shipper.id = :shipperId AND DATE(t.transactionDate) = CURRENT_DATE AND t.status = 'COMPLETED'")
        Long sumTodayEarnings(@Param("shipperId") Long shipperId);

        /**
         * Tìm transaction theo order ID
         */
        List<Transaction> findByOrderId(Long orderId);
}