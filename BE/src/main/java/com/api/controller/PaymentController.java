package com.api.controller;

import com.api.service.MomoPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final MomoPaymentService momoPaymentService;

    @PostMapping("/momo")
    public ResponseEntity<String> createMomoPayment( @RequestParam BigDecimal amount)
    {
        try {
            String payUrl = momoPaymentService.createPaymentUrl( amount);
            return ResponseEntity.ok(payUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
