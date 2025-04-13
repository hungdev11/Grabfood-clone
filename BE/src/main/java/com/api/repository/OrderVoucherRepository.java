package com.api.repository;

import com.api.entity.OrderVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderVoucherRepository extends JpaRepository<OrderVoucher, Long> {
}
