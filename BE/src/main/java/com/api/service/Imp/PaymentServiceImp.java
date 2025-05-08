package com.api.service.Imp;

import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;
import com.api.entity.PaymentInfo;
import com.api.repository.CartDetailRepository;
import com.api.repository.OrderRepository;
import com.api.repository.PaymentInfoRepository;
import com.api.service.MomoPaymentService;
import com.api.service.OrderService;
import com.api.service.PaymentService;
import com.api.service.VNPayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final PaymentInfoRepository paymentInfoRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CartDetailRepository cartDetailRepository;
    private final MomoPaymentService momoPaymentService;
    private final VNPayPaymentService vnPayPaymentService;

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
        paymentInfoRepository.save(paymentInfo);
    }

    @Override
    public OrderResponse createOrderPaymentCod(CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .order(order)
                .paymentAmount(order.getShippingFee().add(order.getTotalPrice()))
                .paymentCode(UUID.randomUUID().toString())
                .paymentName("COD")
                .create_at(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        paymentInfoRepository.save(paymentInfo);

        OrderResponse response = toOrderResponse(order);
        response.setPayment_method("COD");
        return response;
    }

    @Override
    public String createOrderPaymentMomo(CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        try {
            return momoPaymentService.createPaymentUrl(order.getId(), order.getShippingFee().add(order.getTotalPrice()));
        } catch (Exception e) {
            throw new RuntimeException("Can not create momo url");
        }
    }

    @Override
    public String createOrderPaymentVNPay(@RequestBody CreateOrderRequest orderRequest, HttpServletRequest request) {
        Order order = orderService.createOrder(orderRequest);
        return vnPayPaymentService.createPaymentUrl(request, order.getTotalPrice().add(orderRequest.getShippingFee()), order.getId());
    }

    @Override
    public void createPaymentVNPay(BigDecimal amount, Long orderId, String code) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new RuntimeException("Order not found"));
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .create_at(LocalDateTime.now())
                .paymentAmount(amount)
                .paymentCode(UUID.randomUUID().toString())
                .paymentName("VNPAY")
                .status("SUCCESS")
                .order(order)
                .build();
        paymentInfoRepository.save(paymentInfo);
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .address(order.getAddress())
                .note(order.getNote())
                .shippingFee(order.getShippingFee())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .cartDetails(cartDetailRepository.findByOrderId(order.getId()).stream().map(this::toCartDetailResponse).toList())
                .build();
    }

    private CartDetailResponse toCartDetailResponse(CartDetail cartDetail) {
        return CartDetailResponse.builder()
                .id(cartDetail.getId())
                .food_img(cartDetail.getFood().getImage())
                .foodName(cartDetail.getFood().getName())
                .quantity(cartDetail.getQuantity())
                .price(cartDetailRepository.findPriceByFoodId(cartDetail.getFood().getId()))
                .note(cartDetail.getNote())
                .build();
    }
}
