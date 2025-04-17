package com.api.controller;

import com.api.dto.request.MomoNotifyRequest;
import com.api.service.MomoPaymentService;
import com.api.service.OrderService;
import com.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final MomoPaymentService momoPaymentService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    @PostMapping("/momo")
    public ResponseEntity<String> createMomoPayment(@RequestParam Long orderId, @RequestParam BigDecimal amount)
    {
        try {
            String payUrl = momoPaymentService.createPaymentUrl(orderId, amount);
            return ResponseEntity.ok(payUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/momo/notify")
    public ResponseEntity<?> momoNotify(@RequestBody MomoNotifyRequest requestBody) {
        System.out.println("vo ham");
        Long orderId = requestBody.getOrderId();
        String requestId = requestBody.getRequestId();
        long amount = requestBody.getAmount();
        int resultCode = requestBody.getResultCode();
        System.out.println("vo ham");

        if (resultCode == 0) {
            System.out.println("ok");
            paymentService.createPayment(new BigDecimal(amount),orderId,requestId);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("not ok");
            orderService.DeleteOrderFailedPayment(orderId);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/momo/callback")
    public ResponseEntity<String> momoCallback(
            @RequestParam String orderId,
            @RequestParam int resultCode,
            @RequestParam String message
    ) {
        String redirectHtml;
        if (resultCode == 0) {
            redirectHtml = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                            background: linear-gradient(135deg, #e0eafc, #cfdef3);
                        }
                        .container {
                            text-align: center;
                            padding: 30px;
                            background: #fff;
                            border-radius: 15px;
                            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
                            max-width: 500px;
                        }
                        h1 {
                            color: #28a745;
                            font-size: 24px;
                            margin-bottom: 20px;
                        }
                        .spinner {
                            border: 4px solid #f3f3f3;
                            border-top: 4px solid #28a745;
                            border-radius: 50%;
                            width: 30px;
                            height: 30px;
                            animation: spin 1s linear infinite;
                            margin: 20px auto;
                        }
                        @keyframes spin {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(360deg); }
                        }
                        p {
                            color: #666;
                            font-size: 16px;
                        }
                    </style>
                    <script>
                        setTimeout(function() {
                            window.location.href = 'http://localhost:3000';
                        }, 3000);
                    </script>
                </head>
                <body>
                    <div class="container">
                        <h1>Thanh toán thành công!</h1>
                        <p>Bạn sẽ được chuyển về trang chính trong giây lát...</p>
                        <div class="spinner"></div>
                    </div>
                </body>
                </html>
                """;
        } else {
            redirectHtml = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                            background: linear-gradient(135deg, #fce7e7, #f3d7d7);
                        }
                        .container {
                            text-align: center;
                            padding: 30px;
                            background: #fff;
                            border-radius: 15px;
                            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
                            max-width: 500px;
                        }
                        h1 {
                            color: #dc3545;
                            font-size: 24px;
                            margin-bottom: 20px;
                        }
                        .spinner {
                            border: 4px solid #f3f3f3;
                            border-top: 4px solid #dc3545;
                            border-radius: 50%;
                            width: 30px;
                            height: 30px;
                            animation: spin 1s linear infinite;
                            margin: 20px auto;
                        }
                        @keyframes spin {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(360deg); }
                        }
                        p {
                            color: #666;
                            font-size: 16px;
                        }
                    </style>
                    <script>
                        setTimeout(function() {
                            window.location.href = 'http://localhost:3000/checkout';
                        }, 3000);
                    </script>
                </head>
                <body>
                    <div class="container">
                        <h1>Giao dịch không thành công!</h1>
                        <p>Vui lòng thử lại. Bạn sẽ được chuyển về trang trước trong giây lát...</p>
                        <div class="spinner"></div>
                    </div>
                </body>
                </html>
                """;
        }
        return ResponseEntity.ok(redirectHtml);
    }
}
