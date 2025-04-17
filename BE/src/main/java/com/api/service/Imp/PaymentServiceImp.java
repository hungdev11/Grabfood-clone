package com.api.service.Imp;

import com.api.entity.Order;
import com.api.entity.PaymentInfo;
import com.api.repository.OrderRepository;
import com.api.repository.PaymentInfoRepository;
import com.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final PaymentInfoRepository paymentInfoRepository;
    private final OrderRepository orderRepository;
    @Override
    public void updatePaymentStatus(String paymentCode, String status) {
        PaymentInfo payment = paymentInfoRepository.findByPaymentCode(paymentCode);
        if (payment != null) {
            payment.setStatus(status);
            paymentInfoRepository.save(payment);
        }
    }

    @Override
    public void createPayment(BigDecimal amount, Long orderId, String code) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new RuntimeException("Order not found"));
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .create_at(LocalDateTime.now())
                .paymentAmount(amount)
                .paymentName("MOMO")
                .paymentCode(code)
                .status("SUCCESS")
                .order(order)
                .build();
        orderRepository.save(order);
    }
}
