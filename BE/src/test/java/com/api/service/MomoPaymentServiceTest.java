package com.api.service;

import com.api.config.MomoConfig;
import com.api.dto.response.MomoResponse;
import com.api.service.Imp.MomoPaymentServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomoPaymentServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private class TestMomoPaymentService extends MomoPaymentServiceImp {
        @Override
        protected RestTemplate createRestTemplate() {
            return mockRestTemplate;
        }
    }

    private TestMomoPaymentService momoPaymentService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        momoPaymentService = new TestMomoPaymentService();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should create payment URL successfully")
    void createPaymentUrl_Success() throws Exception {
        // Arrange
        Long orderId = 1234L;
        BigDecimal amount = new BigDecimal("150000");

        // Create mock response
        String payUrl = "https://test.momo.vn/pay/12345";
        MomoResponse momoResponse = new MomoResponse();
        momoResponse.setPayUrl(payUrl);

        String jsonResponse = objectMapper.writeValueAsString(momoResponse);

        // Configure mock
        when(mockRestTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(jsonResponse);

        // Act
        String result = momoPaymentService.createPaymentUrl(orderId, amount);

        // Assert
        assertEquals(payUrl, result);
        verify(mockRestTemplate).postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class));
    }

    @Test
    @DisplayName("Should throw exception when MoMo response is null")
    void createPaymentUrl_NullResponse() {
        // Arrange
        Long orderId = 1234L;
        BigDecimal amount = new BigDecimal("150000");

        // Configure mock to return null response
        when(mockRestTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            momoPaymentService.createPaymentUrl(orderId, amount);
        });

        assertTrue(exception.getMessage().contains("Failed to create MOMO payment URL"));
    }

    @Test
    @DisplayName("Should throw exception when MoMo payUrl is null")
    void createPaymentUrl_NullPayUrl() throws Exception {
        // Arrange
        Long orderId = 1234L;
        BigDecimal amount = new BigDecimal("150000");

        // Create mock response with null payUrl
        MomoResponse momoResponse = new MomoResponse();
        momoResponse.setPayUrl(null);

        String jsonResponse = objectMapper.writeValueAsString(momoResponse);

        // Configure mock
        when(mockRestTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(jsonResponse);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            momoPaymentService.createPaymentUrl(orderId, amount);
        });

        assertTrue(exception.getMessage().contains("Failed to create MOMO payment URL"));
    }

    @Test
    @DisplayName("Should throw exception when RestTemplate throws exception")
    void createPaymentUrl_ApiException() {
        // Arrange
        Long orderId = 1234L;
        BigDecimal amount = new BigDecimal("150000");

        // Configure mock to throw exception
        when(mockRestTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))
        ).thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            momoPaymentService.createPaymentUrl(orderId, amount);
        });

        assertTrue(exception.getMessage().contains("Error creating MOMO payment"));
    }
}