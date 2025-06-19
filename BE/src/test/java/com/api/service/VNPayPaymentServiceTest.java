package com.api.service;

import com.api.config.VNPayConfig;
import com.api.service.Imp.VNPayPaymentServiceImp;
import com.api.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VNPayPaymentServiceTest {

    @Mock
    private VNPayConfig vnPayConfig;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private VNPayPaymentServiceImp vnPayPaymentService;

    private Map<String, String> configMap;
    private final String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String secretKey = "TESTSECRETHKEY";
    private final String ipAddress = "127.0.0.1";
    private final String secureHash = "mockedSecureHash";
    private final String queryUrl = "vnp_Amount=1000000&vnp_TxnRef=123";
    private final String hashData = "vnp_Amount=1000000&vnp_TxnRef=123";

    @BeforeEach
    void setUp() {
        configMap = new HashMap<>();
        configMap.put("vnp_Version", "2.1.0");
        configMap.put("vnp_Command", "pay");
        configMap.put("vnp_TmnCode", "TESTMERCHANT");
        configMap.put("vnp_CurrCode", "VND");
        configMap.put("vnp_Locale", "vn");
        configMap.put("vnp_ReturnUrl", "http://localhost:8080/payment/vnpay-return");

        when(vnPayConfig.getVNPayConfig()).thenReturn(configMap);
        when(vnPayConfig.getSecretKey()).thenReturn(secretKey);
        when(vnPayConfig.getVnp_PayUrl()).thenReturn(payUrl);
    }

    @Test
    @DisplayName("Should create payment URL without bank code")
    void createPaymentUrl_WithoutBankCode() {
        // Arrange
        Long orderId = 123L;
        BigDecimal amount = new BigDecimal("10000");
        when(request.getParameter("bankCode")).thenReturn(null);

        try (MockedStatic<VNPayUtil> vnPayUtilMocked = mockStatic(VNPayUtil.class)) {
            vnPayUtilMocked.when(() -> VNPayUtil.getIpAddress(request)).thenReturn(ipAddress);
            vnPayUtilMocked.when(() -> VNPayUtil.getPaymentURL(any(), eq(true))).thenReturn(queryUrl);
            vnPayUtilMocked.when(() -> VNPayUtil.getPaymentURL(any(), eq(false))).thenReturn(hashData);
            vnPayUtilMocked.when(() -> VNPayUtil.hmacSHA512(eq(secretKey), eq(hashData))).thenReturn(secureHash);

            // Act
            String result = vnPayPaymentService.createPaymentUrl(request, amount, orderId);

            // Assert
            String expectedUrl = payUrl + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
            assertEquals(expectedUrl, result);

            // Verify static method parameters
            vnPayUtilMocked.verify(() -> VNPayUtil.getIpAddress(request));
            vnPayUtilMocked.verify(() -> VNPayUtil.getPaymentURL(any(Map.class), eq(true)));
            vnPayUtilMocked.verify(() -> VNPayUtil.getPaymentURL(any(Map.class), eq(false)));
            vnPayUtilMocked.verify(() -> VNPayUtil.hmacSHA512(eq(secretKey), eq(hashData)));
        }
    }

    @Test
    @DisplayName("Should create payment URL with bank code")
    void createPaymentUrl_WithBankCode() {
        // Arrange
        Long orderId = 123L;
        BigDecimal amount = new BigDecimal("10000");
        String bankCode = "NCB";
        when(request.getParameter("bankCode")).thenReturn(bankCode);

        try (MockedStatic<VNPayUtil> vnPayUtilMocked = mockStatic(VNPayUtil.class)) {
            vnPayUtilMocked.when(() -> VNPayUtil.getIpAddress(request)).thenReturn(ipAddress);
            vnPayUtilMocked.when(() -> VNPayUtil.getPaymentURL(any(), anyBoolean())).thenReturn(queryUrl);
            vnPayUtilMocked.when(() -> VNPayUtil.hmacSHA512(eq(secretKey), any())).thenReturn(secureHash);

            // Act
            String result = vnPayPaymentService.createPaymentUrl(request, amount, orderId);

            // Assert
            String expectedUrl = payUrl + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
            assertEquals(expectedUrl, result);

            // Verify VNPayUtil methods were called with correct parameters
            vnPayUtilMocked.verify(() -> VNPayUtil.getIpAddress(request));

            // Don't use verify for getPaymentURL since it's called multiple times
            // Instead, just verify the method was called at least once with the bank code
            vnPayUtilMocked.verify(() -> VNPayUtil.getPaymentURL(argThat(map ->
                    map.containsKey("vnp_BankCode") &&
                            map.get("vnp_BankCode").equals(bankCode)), anyBoolean()), atLeastOnce());
        }
    }

    @Test
    @DisplayName("Should convert amount to VND correctly")
    void createPaymentUrl_AmountConversion() {
        // Arrange
        Long orderId = 123L;
        BigDecimal amount = new BigDecimal("10000");
        long expectedAmountInVND = 1000000; // 10000 * 100

        when(request.getParameter("bankCode")).thenReturn(null);

        try (MockedStatic<VNPayUtil> vnPayUtilMocked = mockStatic(VNPayUtil.class)) {
            vnPayUtilMocked.when(() -> VNPayUtil.getIpAddress(request)).thenReturn(ipAddress);
            vnPayUtilMocked.when(() -> VNPayUtil.getPaymentURL(any(), anyBoolean())).thenReturn(queryUrl);
            vnPayUtilMocked.when(() -> VNPayUtil.hmacSHA512(any(), any())).thenReturn(secureHash);

            // Act
            vnPayPaymentService.createPaymentUrl(request, amount, orderId);

            // Assert - verify amount conversion
            vnPayUtilMocked.verify(() -> VNPayUtil.getPaymentURL(argThat(map ->
                    map.containsKey("vnp_Amount") &&
                            map.get("vnp_Amount").equals(String.valueOf(expectedAmountInVND))), anyBoolean()), atLeastOnce());
        }
    }
}