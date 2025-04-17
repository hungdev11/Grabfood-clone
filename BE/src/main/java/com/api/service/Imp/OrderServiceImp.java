package com.api.service.Imp;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.ApplyVoucherResponse;
import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.mapper.OrderMapper;
import com.api.repository.*;
import com.api.service.CartService;
import com.api.service.OrderService;
import com.api.utils.OrderStatus;
import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private OrderVoucherRepository orderVoucherRepository;

    @Autowired
    private VoucherDetailRepository voucherDetailRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Cart cart = cartRepository.findById(request.getCartId()).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        List<CartDetail> cartDetails = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());
        if (cartDetails.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        User user = userRepository.findById(cart.getUser().getId()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = Order.builder()
                .note(request.getNote())
                .address(request.getAddress())
                .shippingFee(request.getShippingFee())
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(getTotalPrice(cartDetails))
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        //Apply VOucher
        BigDecimal discountedOrderPrice = order.getTotalPrice();
        BigDecimal discountedShippingPrice = order.getShippingFee();
        for (String voucherCode: request.getVoucherCode()) {
            if(voucherCode != null && !voucherCode.isEmpty()) {
                Voucher voucher = voucherRepository.findByCodeAndStatus(voucherCode, VoucherStatus.ACTIVE)
                        .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
                log.info("voucher >>>"+ voucher.getId() + ">>> code >>>" + request.getVoucherCode());
                if (checkApplyVoucher(voucher, order.getTotalPrice())) {
                    OrderVoucher orderVoucher = OrderVoucher.builder()
                            .timeApplied(LocalDateTime.now())
                            .order(order)
                            .voucherDetail(voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now()))
                            .build();
                    if(voucher.getType().equals(VoucherType.PERCENTAGE)) {
                        if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                            discountedOrderPrice = discountedOrderPrice.multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                        } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                            discountedShippingPrice = discountedShippingPrice.multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                        }
                    } else {
                        if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                            discountedOrderPrice = discountedOrderPrice.subtract(voucher.getValue());
                        } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                            discountedShippingPrice = discountedShippingPrice.subtract(voucher.getValue());
                        }
                    }
                    orderVoucherRepository.save(orderVoucher);
                }
            }
        }
        if (discountedShippingPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedShippingPrice = BigDecimal.ZERO;
        }

        if (discountedOrderPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedOrderPrice = BigDecimal.ZERO;
        }
        order.setTotalPrice(discountedOrderPrice);
        order.setShippingFee(discountedShippingPrice);
        orderRepository.save(order);

        for (CartDetail cartDetail: cartDetails) {
            cartDetail.setOrder(order);
            cartDetailRepository.save(cartDetail);
        }

        return orderRepository.findById(order.getId()).orElseThrow(() ->
                new RuntimeException("Order not found")
        );
    }

    @Override
    public List<CartDetail> getCartDetailsByOrder(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getCartDetails();
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getOrders();
    }

    @Override
    public ApplyVoucherResponse applyVoucherToOrder(ApplyVoucherRequest request) {
        if(request.getListCode().isEmpty()) {
            return ApplyVoucherResponse.builder()
                    .discountShippingPrice(BigDecimal.ZERO)
                    .discountOrderPrice(BigDecimal.ZERO)
                    .newOrderPrice(request.getTotalPrice())
                    .newShippingFee(request.getShippingFee())
                    .build();
        }
        BigDecimal discountShippingFee = BigDecimal.ZERO;
        BigDecimal discountOrderPrice = BigDecimal.ZERO;
        BigDecimal orderPrice = request.getTotalPrice();
        BigDecimal shippingFee = request.getShippingFee();
        for (String code: request.getListCode()) {
            Voucher voucher = voucherRepository.findByCodeAndStatus(code, VoucherStatus.ACTIVE).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            log.info("Voucher: "+ voucher.getId().toString());
            checkApplyVoucher(voucher, request.getTotalPrice());
            if(voucher.getType().equals(VoucherType.PERCENTAGE)) {
                if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                    discountOrderPrice = discountOrderPrice.add(orderPrice.multiply(voucher.getValue()).divide(new BigDecimal(100)));
                    orderPrice = orderPrice.multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                    discountShippingFee = discountShippingFee.add(shippingFee.multiply(voucher.getValue()).divide(new BigDecimal(100)));
                    shippingFee = shippingFee.multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                }
            } else {
                if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                    discountOrderPrice = discountOrderPrice.add(voucher.getValue());
                    orderPrice = orderPrice.subtract(voucher.getValue());
                } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                    discountShippingFee = discountShippingFee.add(voucher.getValue());
                    shippingFee = shippingFee.subtract(voucher.getValue());
                }
            }
        }
        if (shippingFee.compareTo(BigDecimal.ZERO) < 0) {
            shippingFee = BigDecimal.ZERO;
            discountShippingFee = request.getShippingFee();
        }

        if (orderPrice.compareTo(BigDecimal.ZERO) < 0) {
            orderPrice = BigDecimal.ZERO;
            discountOrderPrice = request.getTotalPrice();
        }
        return ApplyVoucherResponse.builder()
                .newShippingFee(shippingFee)
                .newOrderPrice(orderPrice)
                .discountOrderPrice(discountOrderPrice)
                .discountShippingPrice(discountShippingFee)
                .build();
    }

    @Override
    public void DeleteOrderFailedPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new RuntimeException("Order not found"));
        List<CartDetail> cartDetailList = order.getCartDetails();
        if(!cartDetailList.isEmpty()) {
            for (CartDetail cartDetail: cartDetailList) {
                cartDetail.setOrder(null);
                cartDetailRepository.save(cartDetail);
            }
        }
        List<OrderVoucher> orderVoucherList = order.getOrderVoucherList();
        if(!orderVoucherList.isEmpty()) {
            for (OrderVoucher orderVoucher: orderVoucherList) {
                orderVoucherRepository.deleteById(orderVoucher.getId());
            }
        }
        orderRepository.delete(order);
    }

    private BigDecimal getTotalPrice(List<CartDetail> cartDetails) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetail cartDetail: cartDetails) {

            BigDecimal price = cartDetailRepository.findPriceByFoodId(cartDetail.getFood().getId());
            List<Long> ids = cartDetail.getIds();
            for (Long id: ids) {
                BigDecimal priceAdd = cartDetailRepository.findPriceByFoodId(id);
                price = price.add(priceAdd);
            }
            int quantity = cartDetail.getQuantity();
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return totalPrice;
    }

    private boolean checkApplyVoucher(Voucher voucher, BigDecimal totalPrice) {
        VoucherDetail voucherDetail= voucher.getVoucherDetails().getFirst();
        log.info("Voucher Detail: " + voucherDetail.getId().toString());
        if(voucher.getRestaurant() != null) {
            return false;
        }
        if (voucherDetail.getEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }
        if(voucher.getMinRequire().compareTo(totalPrice) > 0) {
            throw new AppException(ErrorCode.VOUCHER_MIN_REQUIRE);
        }
        // voucher hết số lượng ....
        return true;
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
