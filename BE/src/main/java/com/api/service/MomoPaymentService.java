package com.api.service;

import java.math.BigDecimal;

public interface MomoPaymentService {
    String createPaymentUrl(BigDecimal amount);
}
