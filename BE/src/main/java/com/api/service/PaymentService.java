package com.api.service;

import java.math.BigDecimal;

public interface PaymentService {
    void updatePaymentStatus(String paymentCode, String status);

    void createPayment(BigDecimal amount, Long orderId, String code);
}
