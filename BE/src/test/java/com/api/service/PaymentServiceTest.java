package com.api.service;

import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Food;
import com.api.entity.Order;
import com.api.entity.PaymentInfo;
import com.api.entity.User;
import com.api.repository.CartDetailRepository;
import com.api.repository.OrderRepository;
import com.api.repository.PaymentInfoRepository;
import com.api.service.Imp.PaymentServiceImp;
import com.api.utils.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentInfoRepository paymentInfoRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private MomoPaymentService momoPaymentService;

    @Mock
    private VNPayPaymentService vnPayPaymentService;

    @Mock
    private HttpServletRequest httpServletRequest;

    private PaymentServiceImp paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImp(
                paymentInfoRepository,
                orderRepository,
                orderService,
                cartDetailRepository,
                momoPaymentService,
                vnPayPaymentService
        );
    }

    @Test
    @DisplayName("Should update payment status successfully")
    void updatePaymentStatus_Success() {
        // Arrange
        String paymentCode = "PAYMENT-123";
        String status = "SUCCESS";

        PaymentInfo mockPayment = new PaymentInfo();
        mockPayment.setPaymentCode(paymentCode);
        mockPayment.setStatus("PENDING");

        when(paymentInfoRepository.findByPaymentCode(paymentCode)).thenReturn(mockPayment);

        // Act
        paymentService.updatePaymentStatus(paymentCode, status);

        // Assert
        ArgumentCaptor<PaymentInfo> paymentCaptor = ArgumentCaptor.forClass(PaymentInfo.class);
        verify(paymentInfoRepository).save(paymentCaptor.capture());

        PaymentInfo updatedPayment = paymentCaptor.getValue();
        assertEquals(status, updatedPayment.getStatus());
    }

    @Test
    @DisplayName("Should not update payment status when payment not found")
    void updatePaymentStatus_PaymentNotFound() {
        // Arrange
        String paymentCode = "PAYMENT-NOT-FOUND";
        String status = "SUCCESS";

        when(paymentInfoRepository.findByPaymentCode(paymentCode)).thenReturn(null);

        // Act
        paymentService.updatePaymentStatus(paymentCode, status);

        // Assert
        verify(paymentInfoRepository, never()).save(any(PaymentInfo.class));
    }

    @Test
    @DisplayName("Should create payment successfully")
    void createPayment_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("150000");
        Long orderId = 1L;
        String code = "MOMO-123";

        Order mockOrder = new Order();
        mockOrder.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Act
        paymentService.createPayment(amount, orderId, code);

        // Assert
        ArgumentCaptor<PaymentInfo> paymentCaptor = ArgumentCaptor.forClass(PaymentInfo.class);
        verify(paymentInfoRepository).save(paymentCaptor.capture());

        PaymentInfo createdPayment = paymentCaptor.getValue();
        assertEquals(amount, createdPayment.getPaymentAmount());
        assertEquals("MOMO", createdPayment.getPaymentName());
        assertEquals(code, createdPayment.getPaymentCode());
        assertEquals("SUCCESS", createdPayment.getStatus());
        assertEquals(mockOrder, createdPayment.getOrder());
        assertNotNull(createdPayment.getCreate_at());
    }

    @Test
    @DisplayName("Should throw exception when creating payment with non-existent order")
    void createPayment_OrderNotFound() {
        // Arrange
        BigDecimal amount = new BigDecimal("150000");
        Long orderId = 999L;
        String code = "MOMO-123";

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(amount, orderId, code);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(paymentInfoRepository, never()).save(any(PaymentInfo.class));
    }

    @Test
    @DisplayName("Should create order with COD payment successfully")
    void createOrderPaymentCod_Success() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingFee(new BigDecimal("25000"));

        Order mockOrder = createMockOrder();
        CartDetail mockCartDetail = createMockCartDetail(mockOrder);

        when(orderService.createOrder(request)).thenReturn(mockOrder);
        when(cartDetailRepository.findByOrderId(mockOrder.getId())).thenReturn(Collections.singletonList(mockCartDetail));
        when(cartDetailRepository.findPriceByFoodId(mockCartDetail.getFood().getId())).thenReturn(new BigDecimal("100000"));

        // Act
        OrderResponse response = paymentService.createOrderPaymentCod(request);

        // Assert
        verify(paymentInfoRepository).save(any(PaymentInfo.class));

        assertEquals(mockOrder.getId(), response.getId());
        assertEquals("COD", response.getPayment_method());
        assertEquals(mockOrder.getTotalPrice(), response.getTotalPrice());
        assertEquals(mockOrder.getShippingFee(), response.getShippingFee());
        assertEquals(1, response.getCartDetails().size());
    }

    @Test
    @DisplayName("Should create order with Momo payment successfully")
    void createOrderPaymentMomo_Success() throws Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingFee(new BigDecimal("25000"));

        Order mockOrder = createMockOrder();
        String expectedPaymentUrl = "https://test-payment.momo.vn/payment/123";

        when(orderService.createOrder(request)).thenReturn(mockOrder);
        when(momoPaymentService.createPaymentUrl(
                eq(mockOrder.getId()),
                eq(mockOrder.getShippingFee().add(mockOrder.getTotalPrice())))
        ).thenReturn(expectedPaymentUrl);

        // Act
        String result = paymentService.createOrderPaymentMomo(request);

        // Assert
        assertEquals(expectedPaymentUrl, result);
        verify(momoPaymentService).createPaymentUrl(
                eq(mockOrder.getId()),
                eq(mockOrder.getShippingFee().add(mockOrder.getTotalPrice()))
        );
    }

    @Test
    @DisplayName("Should throw exception when Momo payment creation fails")
    void createOrderPaymentMomo_Failure() throws Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingFee(new BigDecimal("25000"));

        Order mockOrder = createMockOrder();

        when(orderService.createOrder(request)).thenReturn(mockOrder);
        when(momoPaymentService.createPaymentUrl(anyLong(), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("Momo API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.createOrderPaymentMomo(request);
        });

        assertEquals("Can not create momo url", exception.getMessage());
    }

    @Test
    @DisplayName("Should create order with VNPay payment successfully")
    void createOrderPaymentVNPay_Success() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingFee(new BigDecimal("25000"));

        Order mockOrder = createMockOrder();
        String expectedPaymentUrl = "https://vnpay.vn/payment/123";

        when(orderService.createOrder(request)).thenReturn(mockOrder);
        when(vnPayPaymentService.createPaymentUrl(
                eq(httpServletRequest),
                eq(mockOrder.getTotalPrice().add(request.getShippingFee())),
                eq(mockOrder.getId()))
        ).thenReturn(expectedPaymentUrl);

        // Act
        String result = paymentService.createOrderPaymentVNPay(request, httpServletRequest);

        // Assert
        assertEquals(expectedPaymentUrl, result);
        verify(vnPayPaymentService).createPaymentUrl(
                eq(httpServletRequest),
                eq(mockOrder.getTotalPrice().add(request.getShippingFee())),
                eq(mockOrder.getId())
        );
    }

    @Test
    @DisplayName("Should create VNPay payment info successfully")
    void createPaymentVNPay_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("150000");
        Long orderId = 1L;
        String code = "VNPAY-123";

        Order mockOrder = new Order();
        mockOrder.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Act
        paymentService.createPaymentVNPay(amount, orderId, code);

        // Assert
        ArgumentCaptor<PaymentInfo> paymentCaptor = ArgumentCaptor.forClass(PaymentInfo.class);
        verify(paymentInfoRepository).save(paymentCaptor.capture());

        PaymentInfo createdPayment = paymentCaptor.getValue();
        assertEquals(amount, createdPayment.getPaymentAmount());
        assertEquals("VNPAY", createdPayment.getPaymentName());
        assertNotNull(createdPayment.getPaymentCode()); // Should be a UUID
        assertEquals("SUCCESS", createdPayment.getStatus());
        assertEquals(mockOrder, createdPayment.getOrder());
        assertNotNull(createdPayment.getCreate_at());
    }

    // Helper methods to create mock entities
    private Order createMockOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(new BigDecimal("100000"));
        order.setShippingFee(new BigDecimal("25000"));
        order.setStatus(OrderStatus.PROCESSING);
        order.setAddress("123 Test Street");
        order.setNote("Test note");

        User user = new User();
        user.setId(1L);
        order.setUser(user);

        return order;
    }

    private CartDetail createMockCartDetail(Order order) {
        CartDetail cartDetail = new CartDetail();
        cartDetail.setId(1L);
        cartDetail.setOrder(order);
        cartDetail.setQuantity(2);
        cartDetail.setNote("No spicy");

        Food food = new Food();
        food.setId(1L);
        food.setName("Test Food");
        food.setImage("food.jpg");

        cartDetail.setFood(food);

        return cartDetail;
    }
}