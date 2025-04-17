package com.api.service;

import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.OrderResponse;

import java.math.BigDecimal;

public interface PaymentService {
    void updatePaymentStatus(String paymentCode, String status);

    void createPayment(BigDecimal amount, Long orderId, String code);

    OrderResponse createOrderPaymentCod(CreateOrderRequest request);

    String createOrderPaymentMomo(CreateOrderRequest request);
}
