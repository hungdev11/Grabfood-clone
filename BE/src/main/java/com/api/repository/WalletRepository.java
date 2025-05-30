package com.api.repository;

import com.api.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Tìm wallet theo shipper ID
     */
    Optional<Wallet> findByShipperId(Long shipperId);

    /**
     * Kiểm tra xem shipper có wallet hay chưa
     */
    boolean existsByShipperId(Long shipperId);

    /**
     * Tìm tất cả wallets có cod_holding > 0
     */
    @Query("SELECT w FROM Wallet w WHERE w.codHolding > 0")
    Iterable<Wallet> findWalletsWithCodHolding();

    /**
     * Cập nhật current_balance cho shipper
     */
    @Query("UPDATE Wallet w SET w.currentBalance = :balance WHERE w.shipper.id = :shipperId")
    void updateCurrentBalance(@Param("shipperId") Long shipperId, @Param("balance") Long balance);

    /**
     * Cập nhật cod_holding cho shipper
     */
    @Query("UPDATE Wallet w SET w.codHolding = :codHolding WHERE w.shipper.id = :shipperId")
    void updateCodHolding(@Param("shipperId") Long shipperId, @Param("codHolding") Long codHolding);
}