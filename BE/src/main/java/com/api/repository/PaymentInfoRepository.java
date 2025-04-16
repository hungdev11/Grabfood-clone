package com.api.repository;

import com.api.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    PaymentInfo findByPaymentCode(String paymentCode);
}
