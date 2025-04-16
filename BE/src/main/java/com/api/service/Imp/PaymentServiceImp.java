package com.api.service.Imp;

import com.api.entity.PaymentInfo;
import com.api.repository.PaymentInfoRepository;
import com.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final PaymentInfoRepository paymentInfoRepository;
    @Override
    public void updatePaymentStatus(String paymentCode, String status) {
        PaymentInfo payment = paymentInfoRepository.findByPaymentCode(paymentCode);
        if (payment != null) {
            payment.setStatus(status);
            paymentInfoRepository.save(payment);
        }
    }
}
