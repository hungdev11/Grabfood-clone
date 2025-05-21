package com.api.repository;

import com.api.entity.Restaurant;
import com.api.entity.Voucher;
import com.api.utils.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    List<Voucher> findByRestaurantIdIsNullAndStatusAndMinRequireLessThanEqual(VoucherStatus status, BigDecimal orderValue);

    Optional<Voucher> findByCodeAndStatus(String code, VoucherStatus status);
    List<Voucher> findByRestaurantId(long restaurantId);
    boolean existsByCode(String code);
    boolean existsByCodeAndRestaurant(String code, Restaurant restaurant);

    List<Voucher> findByRestaurantIsNull();
}
