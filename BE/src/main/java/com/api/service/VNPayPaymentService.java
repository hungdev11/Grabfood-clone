package com.api.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface VNPayPaymentService {
    public String createPaymentUrl(HttpServletRequest request, BigDecimal amount, Long orderId);
}
