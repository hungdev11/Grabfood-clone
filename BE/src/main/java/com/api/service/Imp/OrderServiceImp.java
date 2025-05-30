package com.api.service.Imp;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.*;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.*;
import com.api.service.*;
import com.api.service.strategy.OrderNotificationStrategyFactory;
import com.api.service.strategy.OrderStatusNotificationStrategy;
import com.api.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartDetailRepository cartDetailRepository;

    private final OrderVoucherRepository orderVoucherRepository;

    private final VoucherDetailRepository voucherDetailRepository;

    private final VoucherRepository voucherRepository;

    private final ReviewRepository reviewRepository;

    private final FoodService foodService;
    private final ReviewService reviewService;
    private final CartService cartService;
    private final UserService userService;
    private final LocationService locationService;
    private final ShipperService shipperService;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        List<CartDetail> cartDetails = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());
        if (cartDetails.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        User user = userRepository.findById(cart.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = Order.builder()
                .note(request.getNote())
                .address(request.getAddress())
                .shippingFee(request.getShippingFee())
                .user(user)
                .cartDetails(cartDetails)
                .status(OrderStatus.PENDING)
                .totalPrice(getTotalPrice(cartDetails))
                .discountShippingFee(BigDecimal.ZERO)
                .discountOrderPrice(BigDecimal.ZERO)
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        // Apply VOucher
        BigDecimal discountedOrderPrice = order.getTotalPrice();
        BigDecimal discountedShippingPrice = order.getShippingFee();
        BigDecimal discountShippingFee = BigDecimal.ZERO;
        BigDecimal discountOrderPrice = BigDecimal.ZERO;
        for (String voucherCode : request.getVoucherCode()) {
            if (voucherCode != null && !voucherCode.isEmpty()) {
                Voucher voucher = voucherRepository.findByCodeAndStatus(voucherCode, VoucherStatus.ACTIVE)
                        .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
                log.info("voucher >>>" + voucher.getId() + ">>> code >>>" + request.getVoucherCode());
                if (checkApplyVoucher(voucher, order.getTotalPrice())) {
                    VoucherDetail detail = voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(),
                            LocalDateTime.now());
                    OrderVoucher orderVoucher = OrderVoucher.builder()
                            .timeApplied(LocalDateTime.now())
                            .order(order)
                            .voucherDetail(detail)
                            .build();
                    if (voucher.getType().equals(VoucherType.PERCENTAGE)) {
                        if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                            discountedOrderPrice = discountedOrderPrice.multiply(
                                    (new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                            discountOrderPrice = discountOrderPrice
                                    .add(discountedOrderPrice.multiply(voucher.getValue()).divide(new BigDecimal(100)));
                        } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                            discountedShippingPrice = discountedShippingPrice.multiply(
                                    (new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                            discountShippingFee = discountShippingFee.add(
                                    discountedShippingPrice.multiply(voucher.getValue()).divide(new BigDecimal(100)));

                        }
                    } else {
                        if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                            discountedOrderPrice = discountedOrderPrice.subtract(voucher.getValue());
                            discountOrderPrice = discountOrderPrice.add(voucher.getValue());
                        } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                            discountedShippingPrice = discountedShippingPrice.subtract(voucher.getValue());
                            discountShippingFee = discountShippingFee.add(voucher.getValue());
                        }
                    }
                    detail.setQuantity(detail.getQuantity() - 1);
                    voucherDetailRepository.save(detail);
                    orderVoucherRepository.save(orderVoucher);
                }
            }
        }
        if (discountedShippingPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedShippingPrice = BigDecimal.ZERO;
            discountShippingFee = request.getShippingFee();

        }

        if (discountedOrderPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedOrderPrice = BigDecimal.ZERO;
            discountOrderPrice = getTotalPrice(cartDetails);
        }
        order.setTotalPrice(discountedOrderPrice);
        order.setShippingFee(discountedShippingPrice);
        order.setDiscountOrderPrice(discountOrderPrice);
        order.setDiscountShippingFee(discountShippingFee);
        orderRepository.save(order);

        for (CartDetail cartDetail : cartDetails) {
            cartDetail.setOrder(order);
            cartDetailRepository.save(cartDetail);
        }
        return orderRepository.findById(order.getId()).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    public List<CartDetail> getCartDetailsByOrder(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return order.getCartDetails();
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getOrders();
    }

    @Override
    public ApplyVoucherResponse applyVoucherToOrder(ApplyVoucherRequest request) {
        if (request.getListCode().isEmpty()) {
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
        for (String code : request.getListCode()) {
            Voucher voucher = voucherRepository.findByCodeAndStatus(code, VoucherStatus.ACTIVE)
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            log.info("Voucher: " + voucher.getId().toString());
            checkApplyVoucher(voucher, request.getTotalPrice());
            if (voucher.getType().equals(VoucherType.PERCENTAGE)) {
                if (voucher.getApplyType().equals(VoucherApplyType.ORDER)) {
                    discountOrderPrice = discountOrderPrice
                            .add(orderPrice.multiply(voucher.getValue()).divide(new BigDecimal(100)));
                    orderPrice = orderPrice
                            .multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
                } else if (voucher.getApplyType().equals(VoucherApplyType.SHIPPING)) {
                    discountShippingFee = discountShippingFee
                            .add(shippingFee.multiply(voucher.getValue()).divide(new BigDecimal(100)));
                    shippingFee = shippingFee
                            .multiply((new BigDecimal(100).subtract(voucher.getValue())).divide(new BigDecimal(100)));
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
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        List<CartDetail> cartDetailList = order.getCartDetails();
        if (!cartDetailList.isEmpty()) {
            for (CartDetail cartDetail : cartDetailList) {
                cartDetail.setOrder(null);
                cartDetailRepository.save(cartDetail);
            }
        }
        List<OrderVoucher> orderVoucherList = order.getOrderVoucherList();
        if (!orderVoucherList.isEmpty()) {
            for (OrderVoucher orderVoucher : orderVoucherList) {
                VoucherDetail detail = orderVoucher.getVoucherDetail();
                detail.setQuantity(detail.getQuantity() + 1);
                voucherDetailRepository.save(detail);
                orderVoucherRepository.deleteById(orderVoucher.getId());
            }
        }
        orderRepository.delete(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.info("IN ORDER SERVICE");
        var order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order {} not found", orderId);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        });
        log.info("Order id {}", orderId);
        log.info("Order loaded: {}", order);
        log.info("CartDetails: {}", order.getCartDetails());
        return order;
    }

    @Override
    public List<Order> listAllOrdersOfRestaurant(Long restaurantId) {
        log.info("get all orders of restaurant {}", restaurantId);
        return orderRepository.findAllById(
                orderRepository.getAllOrdersOfRestaurant(restaurantId));
    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getUserOrderByStatus(Long userId, OrderStatus status) {
        List<Order> orderList = orderRepository.getOrderByUserIdAndStatusOrderByIdDesc(userId, status);
        return orderList.stream().map(order -> OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .status(order.getStatus())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .restaurantId(getRestaurantByOrder(order).getId())
                .restaurantName(getRestaurantByOrder(order).getName())
                .discountOrderPrice(order.getDiscountOrderPrice())
                .discountShippingFee(order.getDiscountShippingFee())
                .isReview(reviewRepository.existsByOrder(order)
                        || order.getOrderDate().plusDays(10).isBefore(LocalDateTime.now()))
                .cartDetails(order.getCartDetails().stream().map(this::toCartDetailResponse).toList())
                .build()).toList();
    }

    @Override
    public List<OrderResponse> getUserOrder(Long userId) {
        List<Order> orderList = orderRepository.getOrderByUserIdOrderByIdDesc(userId);
        return orderList.stream().map(order -> OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .status(order.getStatus())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .restaurantId(getRestaurantByOrder(order).getId())
                .restaurantName(getRestaurantByOrder(order).getName())
                .discountOrderPrice(order.getDiscountOrderPrice())
                .discountShippingFee(order.getDiscountShippingFee())
                .isReview(reviewRepository.existsByOrder(order)
                        || order.getOrderDate().plusDays(10).isBefore(LocalDateTime.now()))
                .cartDetails(order.getCartDetails().stream().map(this::toCartDetailResponse).toList())
                .build()).toList();
    }

    @Override
    public PageResponse<GetOrderGroupResponse> getRestaurantOrders(long restaurantId, int page, int size,
            String status) {
        List<String> statusList = Arrays.stream(OrderStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        List<Order> orders = listAllOrdersOfRestaurant(restaurantId);

        if (status != null && !status.isBlank()) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().name().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        orders.sort(Comparator.comparing(Order::getOrderDate).reversed());

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = PageUtils.convertListToPage(orders, pageable);

        Map<Long, Review> reviewMap;
        if (OrderStatus.COMPLETED.name().equalsIgnoreCase(status)) {
            List<Review> reviews = reviewRepository.findAllByOrderIn(orderPage.getContent());
            reviewMap = reviews.stream()
                    .collect(Collectors.toMap(
                            review -> review.getOrder().getId(),
                            review -> review));
        } else {
            reviewMap = Map.of();
        }

        List<OrderResponse> responses = orderPage.getContent()
                .stream()
                .map(order -> {
                    OrderResponse reponse = OrderResponse.builder()
                            .id(order.getId())
                            .userName(order.getUser().getName())
                            .address(order.getAddress())
                            .status(order.getStatus())
                            .note(order.getNote())
                            .totalPrice(order.getTotalPrice())
                            .shippingFee(order.getShippingFee())
                            .discountOrderPrice(order.getDiscountOrderPrice())
                            .discountShippingFee(order.getDiscountShippingFee())
                            .createdAt(order.getOrderDate())
                            // .isReview(reviewRepository.existsByOrder(order) ||
                            // order.getOrderDate().plusDays(10).isBefore(LocalDateTime.now()))
                            .cartDetails(order.getCartDetails()
                                    .stream()
                                    .map(this::toCartDetailResponse)
                                    .toList())
                            .build();
                    if (!reviewMap.isEmpty() && reviewMap.containsKey(order.getId())) {
                        reponse.setReviewResponse(reviewService.buildReviewResponse(reviewMap.get(order.getId())));
                    }
                    return reponse;
                })
                .toList();

        return PageResponse.<GetOrderGroupResponse>builder()
                .page(orderPage.getNumber())
                .items(
                        GetOrderGroupResponse.builder()
                                .statusList(statusList)
                                .orders(responses)
                                .build())
                .total(orderPage.getTotalElements())
                .build();
    }

    private BigDecimal getTotalPrice(List<CartDetail> cartDetails) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetail cartDetail : cartDetails) {

            BigDecimal price = foodService.getFoodPriceIn(cartDetail.getFood().getId(), LocalDateTime.now());
            List<Long> ids = cartDetail.getIds();
            for (Long id : ids) {
                BigDecimal priceAdd = foodService.getFoodPriceIn(id, LocalDateTime.now());
                price = price.add(priceAdd);
            }
            int quantity = cartDetail.getQuantity();
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return totalPrice;
    }

    private boolean checkApplyVoucher(Voucher voucher, BigDecimal totalPrice) {
        VoucherDetail voucherDetail = voucher.getVoucherDetails().getFirst();
        log.info("Voucher Detail: " + voucherDetail.getId().toString());
        if (voucher.getRestaurant() != null) {
            return false;
        }
        if (voucherDetail.getEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }
        if (voucher.getMinRequire().compareTo(totalPrice) > 0) {
            throw new AppException(ErrorCode.VOUCHER_MIN_REQUIRE);
        }
        // voucher hết số lượng ....
        return true;
    }

    private CartDetailResponse toCartDetailResponse(CartDetail cartDetail) {
        BigDecimal totalPrice = foodService.getFoodPriceIn(cartDetail.getFood().getId(),
                cartDetail.getOrder().getOrderDate());
        List<Long> ids = cartDetail.getIds();
        List<AdditionalFoodCartResponse> additionalFoodCartResponses = new ArrayList<>();
        for (Long id : ids) {
            GetFoodResponse food = foodService.getFood(id, true);
            if (food != null) {
                totalPrice = totalPrice.add(foodService.getFoodPriceIn(id, cartDetail.getOrder().getOrderDate()));
                AdditionalFoodCartResponse response = AdditionalFoodCartResponse.builder()
                        .id(id)
                        .name(food.getName())
                        .build();
                additionalFoodCartResponses.add(response);
            }
        }
        return CartDetailResponse.builder()
                .id(cartDetail.getId())
                .food_img(cartDetail.getFood().getImage())
                .foodName(cartDetail.getFood().getName())
                .quantity(cartDetail.getQuantity())
                .price(totalPrice)
                .note(cartDetail.getNote())
                .additionFoods(additionalFoodCartResponses)
                .build();
    }

    private Restaurant getRestaurantByOrder(Order order) {
        if (!order.getCartDetails().isEmpty()) {
            return order.getCartDetails().getFirst().getFood().getRestaurant();
        } else {
            return new Restaurant();
        }
    }

    @Override
    @Transactional
    public boolean reorder(long userId, long orderId) {
        Order order = getOrderById(orderId);
        if (order.getUser().getId() != userId) {
            log.error("Order id {} is not the belong to user id {}", orderId, userId);
            throw new AppException(ErrorCode.ORDER_NOT_BELONG_TO_CUSTOMER);
        }
        // reorder with only completed orders
        if (!order.getStatus().equals(OrderStatus.COMPLETED)) {
            throw new AppException(ErrorCode.ORDER_NOT_ELIGIBLE_FOR_REORDER);
        }
        Optional<Cart> cartOpt = cartRepository.findByUser(userService.getUserById(userId));
        List<CartDetail> currentCartDetails = cartOpt.isPresent() ? cartOpt.get().getCartDetails() : new ArrayList<>();
        List<CartDetail> oldCartDetailsWithAvailableFood = order.getCartDetails()
                .stream()
                .filter(cd -> cd.getFood().getStatus().equals(FoodStatus.ACTIVE))
                .toList();

        if (oldCartDetailsWithAvailableFood.isEmpty()) {
            log.warn("Cannot reorder without available food");
            return false;
        }

        // clear current cart
        if (currentCartDetails.size() > 0) {
            log.info("Clear before reorder");
            cartService.clearCart(cartOpt.get());
        }

        // add reorder cart item ?
        Set<Long> additionalIdsStillAvailable = foodService
                .getAdditionalFoodsOfRestaurant(
                        oldCartDetailsWithAvailableFood.getFirst().getFood().getRestaurant().getId(),
                        true, 0, 100)
                .getItems()
                .stream()
                .map(GetFoodResponse::getId)
                .collect(Collectors.toSet());
        List<CartDetail> newCartDetails = oldCartDetailsWithAvailableFood.stream()
                .map(ocd -> {
                    CartDetail newCD = CartDetail.builder()
                            .ids(new ArrayList<>())
                            .cart(ocd.getCart())
                            .food(ocd.getFood())
                            .note(ocd.getNote())
                            .quantity(ocd.getQuantity())
                            .order(null)
                            .build();
                    if (ocd.getIds() != null) {
                        for (long id : ocd.getIds()) {
                            if (additionalIdsStillAvailable.contains(id)) {
                                newCD.getIds().add(id);
                            }
                        }
                    }
                    cartDetailRepository.save(newCD);
                    return newCD;
                })
                .toList();
        cartOpt.get().getCartDetails().addAll(newCartDetails);
        cartRepository.save(cartOpt.get());
        log.info("Reorder completed");
        // move to checkout or do something to notify customer
        return !cartOpt.get().getCartDetails().isEmpty();
    }

    @Override
    public CheckDistanceResponse checkDistanceOrder(long userId, double lat, double lon) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<CartDetail> cartDetails = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());
        if (!cartDetails.isEmpty()) {
            Restaurant restaurant = cartDetails.getFirst().getFood().getRestaurant();
            LocationDistanceResponse locationDistanceResponse = locationService.getDistance(lat, lon,
                    restaurant.getAddress().getLat(), restaurant.getAddress().getLon());
            return CheckDistanceResponse.builder()
                    .check(locationDistanceResponse.getDistance() < 10000.00)
                    .distance(locationDistanceResponse.getDistance())
                    .duration(locationDistanceResponse.getDuration())
                    .shippingFee(locationDistanceResponse.getShippingFee())
                    .build();
        }
        return CheckDistanceResponse.builder()
                .check(false)
                .distance(-1.0)
                .duration(-1.0)
                .shippingFee(BigDecimal.ZERO)
                .build();
    }

    @Override
    public PageResponse<List<OrderResponse>> getOrderAdmin(int page, int size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(paging);

        List<OrderResponse> responseList = orderPage.getContent().stream().map(order -> {
            OrderResponse reponse = OrderResponse.builder()
                    .id(order.getId())
                    .userName(order.getUser().getName())
                    .address(order.getAddress())
                    .status(order.getStatus())
                    .note(order.getNote())
                    .totalPrice(order.getTotalPrice())
                    .shippingFee(order.getShippingFee())
                    .discountOrderPrice(order.getDiscountOrderPrice())
                    .discountShippingFee(order.getDiscountShippingFee())
                    .createdAt(order.getOrderDate())
                    .restaurantName(order.getCartDetails().getFirst().getFood().getRestaurant().getName())
                    .cartDetails(order.getCartDetails()
                            .stream()
                            .map(this::toCartDetailResponse)
                            .toList())
                    .build();
            return reponse;
        }).toList();
        return PageResponse.<List<OrderResponse>>builder()
                .items(responseList)
                .page(page)
                .size(size)
                .total(orderPage.getTotalElements())
                .build();
    }

    @Override
    public void cancelOrder(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ===== SHIPPER ORDER MANAGEMENT IMPLEMENTATIONS =====

    @Override
    public Page<ShipperOrderResponse> getOrdersForShipper(String shipperPhone, String status, int page, int size) {
        try {
            // Get shipper by phone
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Create pageable
            Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

            Page<Order> orderPage;
            if (status != null && !status.isEmpty()) {
                // Filter by status
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orderPage = orderRepository.findOrdersByShipperIdAndStatus(shipper.getId(), orderStatus, pageable);
            } else {
                // All orders
                orderPage = orderRepository.findOrdersByShipperId(shipper.getId(), pageable);
            }

            // Convert to response
            return orderPage.map(this::convertToShipperOrderResponse);

        } catch (Exception e) {
            log.error("Error getting orders for shipper {}", shipperPhone, e);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
    }

    @Override
    public ShipperOrderResponse getOrderDetailForShipper(Long orderId, String shipperPhone) {
        try {
            // Get shipper by phone
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Find order and verify permission
            Order order = orderRepository.findOrderByIdAndShipperId(orderId, shipper.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            return convertToShipperOrderResponse(order);

        } catch (Exception e) {
            log.error("Error getting order detail {} for shipper {}", orderId, shipperPhone, e);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public String acceptOrderByShipper(Long orderId, String shipperPhone) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Find order that can be accepted
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            // Verify order can be accepted
            if (order.getShipper() != null && !order.getShipper().getId().equals(shipper.getId())) {
                throw new AppException(ErrorCode.ORDER_ALREADY_ASSIGNED);
            }

            if (!canAcceptOrder(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_ACCEPTED);
            }

            // Accept order
            order.setShipper(shipper);
            order.setStatus(OrderStatus.SHIPPING);
            order.setAcceptedAt(LocalDateTime.now());
            orderRepository.save(order);

            log.info("Shipper {} accepted order {}", shipperPhone, orderId);
            return "Order accepted successfully";

        } catch (Exception e) {
            log.error("Error accepting order {} by shipper {}", orderId, shipperPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String rejectOrderByShipper(Long orderId, String shipperPhone, String reason) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Find order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            // Verify permission - shipper can reject if assigned to them
            if (order.getShipper() == null || !order.getShipper().getId().equals(shipper.getId())) {
                throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
            }

            if (!canRejectOrder(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_REJECTED);
            }

            // Reject order - remove assignment and set back to available
            order.setShipper(null);
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            log.info("Shipper {} rejected order {} with reason: {}", shipperPhone, orderId, reason);
            return "Order rejected successfully";

        } catch (Exception e) {
            log.error("Error rejecting order {} by shipper {}", orderId, shipperPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String pickupOrderByShipper(Long orderId, String shipperPhone) {
        try {
            // Get shipper and verify permission
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);
            Order order = orderRepository.findOrderByIdAndShipperId(orderId, shipper.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!canPickupOrder(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_PICKED_UP);
            }

            // Update status to shipping
            order.setStatus(OrderStatus.SHIPPING);
            order.setPickedUpAt(LocalDateTime.now());
            orderRepository.save(order);

            log.info("Shipper {} picked up order {}", shipperPhone, orderId);
            return "Order picked up successfully";

        } catch (Exception e) {
            log.error("Error picking up order {} by shipper {}", orderId, shipperPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String completeOrderByShipper(Long orderId, String shipperPhone) {
        try {
            // Get shipper and verify permission
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);
            Order order = orderRepository.findOrderByIdAndShipperId(orderId, shipper.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!canCompleteOrder(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_COMPLETED);
            }

            // Complete order
            order.setStatus(OrderStatus.COMPLETED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);

            // Update shipper stats
            updateShipperStatsOnCompletion(shipper);

            log.info("Shipper {} completed order {}", shipperPhone, orderId);
            return "Order completed successfully";

        } catch (Exception e) {
            log.error("Error completing order {} by shipper {}", orderId, shipperPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String cancelOrderByShipper(Long orderId, String shipperPhone) {
        try {
            // Get shipper and verify permission
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);
            Order order = orderRepository.findOrderByIdAndShipperId(orderId, shipper.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!canCancelOrder(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
            }

            // Cancel order
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            log.info("Shipper {} cancelled order {}", shipperPhone, orderId);
            return "Order cancelled successfully";

        } catch (Exception e) {
            log.error("Error cancelling order {} by shipper {}", orderId, shipperPhone, e);
            throw e;
        }
    }

    // ===== HELPER METHODS =====

    private ShipperOrderResponse convertToShipperOrderResponse(Order order) {
        Restaurant restaurant = getRestaurantByOrder(order);

        List<ShipperOrderResponse.OrderItemResponse> items = order.getCartDetails().stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());

        return ShipperOrderResponse.builder()
                .id(order.getId())
                .customerName(order.getUser().getName())
                .customerPhone(order.getUser().getPhone())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : "COD")
                .restaurantName(restaurant.getName())
                .restaurantPhone(restaurant.getPhone())
                .restaurantAddress(restaurant.getAddress().getDetail())
                .restaurantLatitude(restaurant.getAddress().getLat())
                .restaurantLongitude(restaurant.getAddress().getLon())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .distance(order.getDistance())
                .estimatedTime(order.getEstimatedTime())
                .assignedAt(order.getAssignedAt())
                .acceptedAt(order.getAcceptedAt())
                .pickedUpAt(order.getPickedUpAt())
                .deliveredAt(order.getDeliveredAt())
                .shipperEarning(order.getShipperEarning())
                .tip(order.getTip())
                .gemsEarned(order.getGemsEarned())
                .items(items)
                .build();
    }

    private ShipperOrderResponse.OrderItemResponse convertToOrderItemResponse(CartDetail cartDetail) {
        List<String> additionalFoods = new ArrayList<>();
        if (cartDetail.getIds() != null) {
            for (Long id : cartDetail.getIds()) {
                try {
                    GetFoodResponse food = foodService.getFood(id, true);
                    if (food != null) {
                        additionalFoods.add(food.getName());
                    }
                } catch (Exception e) {
                    log.warn("Could not get additional food with id {}", id);
                }
            }
        }

        BigDecimal price = foodService.getFoodPriceIn(cartDetail.getFood().getId(),
                cartDetail.getOrder().getOrderDate());

        return ShipperOrderResponse.OrderItemResponse.builder()
                .foodName(cartDetail.getFood().getName())
                .quantity(cartDetail.getQuantity())
                .price(price)
                .note(cartDetail.getNote())
                .additionalFoods(additionalFoods)
                .build();
    }

    private boolean canAcceptOrder(OrderStatus status) {
        return status == OrderStatus.PROCESSING || status == OrderStatus.READY_FOR_PICKUP;
    }

    private boolean canRejectOrder(OrderStatus status) {
        return status == OrderStatus.PROCESSING || status == OrderStatus.READY_FOR_PICKUP;
    }

    private boolean canPickupOrder(OrderStatus status) {
        return status == OrderStatus.READY_FOR_PICKUP || status == OrderStatus.PROCESSING;
    }

    private boolean canCompleteOrder(OrderStatus status) {
        return status == OrderStatus.SHIPPING;
    }

    private boolean canCancelOrder(OrderStatus status) {
        return status == OrderStatus.PROCESSING || status == OrderStatus.READY_FOR_PICKUP ||
                status == OrderStatus.SHIPPING;
    }

    private void updateShipperStatsOnCompletion(Shipper shipper) {
        shipper.setCompletedOrders(shipper.getCompletedOrders() + 1);
        shipper.setTotalOrders(shipper.getTotalOrders() + 1);

        // Recalculate acceptance rate
        if (shipper.getTotalOrders() > 0) {
            float acceptanceRate = (float) shipper.getCompletedOrders() / shipper.getTotalOrders() * 100;
            shipper.setAcceptanceRate(acceptanceRate);
        }

        // Save updated shipper stats would be handled by ShipperService
        log.info("Updated stats for shipper {}: completed={}, total={}",
                shipper.getPhone(), shipper.getCompletedOrders(), shipper.getTotalOrders());
    }
}
