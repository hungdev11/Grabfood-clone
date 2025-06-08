package com.api.repository;

import com.api.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository để quản lý thông tin ví của shipper
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    // Tìm ví theo shipper ID - sử dụng custom query
    @Query("SELECT w FROM Wallet w WHERE w.shipper.id = :shipperId")
    Optional<Wallet> findByShipperId(@Param("shipperId") Long shipperId);
    
    // Kiểm tra tồn tại ví theo shipper ID - sử dụng custom query
    @Query("SELECT COUNT(w) > 0 FROM Wallet w WHERE w.shipper.id = :shipperId")
    boolean existsByShipperId(@Param("shipperId") Long shipperId);
    
    // Lấy tổng số dư của tất cả ví
    @Query("SELECT COALESCE(SUM(w.currentBalance), 0) FROM Wallet w")
    Long getTotalBalance();
    
    // Lấy tổng thu nhập hôm nay của tất cả shipper
    @Query("SELECT COALESCE(SUM(w.todayEarnings), 0) FROM Wallet w")
    Long getTotalTodayEarnings();
    
    // Lấy danh sách ví có số dư lớn nhất
    @Query("SELECT w FROM Wallet w ORDER BY w.currentBalance DESC")
    List<Wallet> findWalletsOrderByBalance();
    
    // Lấy danh sách ví có thu nhập hôm nay cao nhất
    @Query("SELECT w FROM Wallet w ORDER BY w.todayEarnings DESC")
    List<Wallet> findWalletsOrderByTodayEarnings();
    
    // Lấy danh sách ví có COD holding
    @Query("SELECT w FROM Wallet w WHERE w.codHolding > 0")
    List<Wallet> findWalletsWithCodHolding();
    
    // Lấy tổng COD đang giữ của tất cả shipper
    @Query("SELECT COALESCE(SUM(w.codHolding), 0) FROM Wallet w")
    Long getTotalCodHolding();
    
    // Lấy danh sách ví được cập nhật trong khoảng thời gian
    @Query("SELECT w FROM Wallet w WHERE w.lastUpdated BETWEEN :startTime AND :endTime")
    List<Wallet> findWalletsUpdatedBetween(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);
    
    // Đếm số ví có thể rút tiền (có số dư)
    @Query("SELECT COUNT(w) FROM Wallet w WHERE w.currentBalance > 0")
    Long countWalletsWithBalance();
    
    // Lấy ví theo khoảng số dư
    @Query("SELECT w FROM Wallet w WHERE w.currentBalance BETWEEN :minBalance AND :maxBalance")
    List<Wallet> findWalletsByBalanceRange(@Param("minBalance") Long minBalance, 
                                          @Param("maxBalance") Long maxBalance);
    
    // Lấy ví có thu nhập tháng này cao nhất
    @Query("SELECT w FROM Wallet w ORDER BY w.monthEarnings DESC")
    List<Wallet> findTopEarnersThisMonth();
    
    // Lấy ví của shipper được cập nhật gần đây nhất
    @Query("SELECT w FROM Wallet w WHERE w.shipper.id = :shipperId ORDER BY w.lastUpdated DESC")
    Optional<Wallet> findLatestWalletByShipperId(@Param("shipperId") Long shipperId);
} 