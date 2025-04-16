package com.api.service;

public interface PaymentService {
    void updatePaymentStatus(String paymentCode, String status);
}
