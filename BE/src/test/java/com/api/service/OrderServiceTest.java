package com.api.service;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.request.CreateOrderRequest;
import com.api.dto.response.*;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.*;
import com.api.service.Imp.OrderServiceImp;
import com.api.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private OrderVoucherRepository orderVoucherRepository;
    @Mock
    private VoucherDetailRepository voucherDetailRepository;
    @Mock
    private VoucherRepository voucherRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private FoodService foodService;
    @Mock
    private UserService userService;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private OrderServiceImp orderService;

    private User testUser;
    private Cart testCart;
    private Order testOrder;
    private CartDetail testCartDetail;
    private Food testFood;
    private FoodDetail testFoodDetail;
    private Restaurant testRestaurant;
    private Address testAddress;
    private CreateOrderRequest createOrderRequest;
    private Voucher testVoucher;
    private VoucherDetail testVoucherDetail;
    private final LocalDateTime fixedTime = LocalDateTime.of(2025, 6, 14, 13, 27, 10);

    @BeforeEach
    void setUp() {
        // Setup test entities
        testUser = User.builder()
                .name("Test User")
                .build();
        testUser.setId(1L);

        testAddress = Address.builder()
                .lat(10.0)
                .lon(20.0)
                .build();

        testRestaurant = Restaurant.builder()
                .name("Test Restaurant")
                .address(testAddress)
                .build();
        testRestaurant.setId(1L);

        testFood = Food.builder()
                .name("Test Food")
                .restaurant(testRestaurant)
                .status(FoodStatus.ACTIVE)
                .build();
        testFood.setId(1L);

        testFoodDetail = FoodDetail.builder()
                .price(BigDecimal.valueOf(25))
                .startTime(fixedTime.minusDays(1))
                .endTime(null)
                .food(testFood)
                .build();
        testFoodDetail.setId(1L);

        testCart = Cart.builder()
                .user(testUser)
                .cartDetails(new ArrayList<>())
                .build();
        testCart.setId(1L);

        testCartDetail = CartDetail.builder()
                .cart(testCart)
                .food(testFood)
                .quantity(2)
                .note("Test note")
                .ids(Arrays.asList())
                .build();
        testCartDetail.setId(1L);

        testOrder = Order.builder()
                .user(testUser)
                .cartDetails(Arrays.asList(testCartDetail))
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .discountOrderPrice(BigDecimal.ZERO)
                .discountShippingFee(BigDecimal.ZERO)
                .orderDate(LocalDateTime.now())
                .address("Test Address")
                .latitude(10.0)
                .longitude(20.0)
                .orderDate(LocalDateTime.now())
                .build();
        testOrder.setId(1L);
        testCartDetail.setOrder(testOrder);

        testVoucherDetail = VoucherDetail.builder()
                .quantity(10)
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        testVoucherDetail.setId(1L);

        testVoucher = Voucher.builder()
                .code("TEST123")
                .status(VoucherStatus.ACTIVE)
                .type(VoucherType.PERCENTAGE)
                .applyType(VoucherApplyType.ORDER)
                .value(new BigDecimal("20"))
                .minRequire(new BigDecimal("50"))
                .restaurant(null)
                .voucherDetails(Arrays.asList(testVoucherDetail))
                .build();
        testVoucher.setId(1L);

        createOrderRequest = CreateOrderRequest.builder()
                .cartId(1L)
                .note("Test order note")
                .address("Test order address")
                .shippingFee(new BigDecimal("10.00"))
                .lat(10.0)
                .lon(20.0)
                .voucherCode(Arrays.asList("TEST123"))
                .build();
    }

    @Test
    void createOrder_Success_WithoutVoucher() {
        // Given
        createOrderRequest.setVoucherCode(new ArrayList<>()); // Không sử dụng voucher

        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(List.of(testCartDetail));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(foodService.getFoodPriceIn(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("25.00"));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.createOrder(createOrderRequest);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(cartDetailRepository).save(any(CartDetail.class));

        BigDecimal expectedTotal = new BigDecimal("50.00");
        BigDecimal actualTotal = orderService.getTotalPrice(List.of(testCartDetail));
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void createOrder_Success_WithVoucher() {
        // Given
        createOrderRequest.setVoucherCode(List.of("TEST123")); // đảm bảo request có mã voucher

        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(List.of(testCartDetail));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voucherRepository.findByCodeAndStatus("TEST123", VoucherStatus.ACTIVE)).thenReturn(Optional.of(testVoucher));
        when(voucherDetailRepository.findByVoucherIdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(testVoucherDetail);

        when(foodService.getFoodPriceIn(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("50.00"));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.createOrder(createOrderRequest);

        // Then
        assertNotNull(result);
        verify(voucherDetailRepository).save(any(VoucherDetail.class));
        verify(orderVoucherRepository).save(any(OrderVoucher.class));
        verify(orderRepository, times(2)).save(any(Order.class));
    }


    @Test
    void createOrder_ThrowsException_CartNotFound() {
        // Given
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.createOrder(createOrderRequest));
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createOrder_ThrowsException_CartEmpty() {
        // Given
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.createOrder(createOrderRequest));
        assertEquals(ErrorCode.CART_EMPTY, exception.getErrorCode());
    }

    @Test
    void createOrder_ThrowsException_UserNotFound() {
        // Given
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(Arrays.asList(testCartDetail));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.createOrder(createOrderRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createOrder_ThrowsException_VoucherNotFound() {
        // Given
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(Arrays.asList(testCartDetail));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voucherRepository.findByCodeAndStatus("TEST123", VoucherStatus.ACTIVE)).thenReturn(Optional.empty());
        when(foodService.getFoodPriceIn(anyLong(), any(LocalDateTime.class))).thenReturn(new BigDecimal("50.00"));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.createOrder(createOrderRequest));
        assertEquals(ErrorCode.VOUCHER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void applyVoucherToOrder_Success_WithPercentageVoucher() {
        // Given
        ApplyVoucherRequest request = ApplyVoucherRequest.builder()
                .listCode(Arrays.asList("TEST123"))
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .build();

        when(voucherRepository.findByCodeAndStatus("TEST123", VoucherStatus.ACTIVE)).thenReturn(Optional.of(testVoucher));

        // When
        ApplyVoucherResponse result = orderService.applyVoucherToOrder(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("80.000"), result.getNewOrderPrice());
        assertEquals(new BigDecimal("20.00"), result.getDiscountOrderPrice());
        assertEquals(new BigDecimal("10.00"), result.getNewShippingFee());
        assertEquals(BigDecimal.ZERO, result.getDiscountShippingPrice());
    }

    @Test
    void applyVoucherToOrder_Success_WithFixedAmountVoucher() {
        // Given
        testVoucher.setType(VoucherType.FIXED);
        testVoucher.setValue(new BigDecimal("15.00"));

        ApplyVoucherRequest request = ApplyVoucherRequest.builder()
                .listCode(Arrays.asList("TEST123"))
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .build();

        when(voucherRepository.findByCodeAndStatus("TEST123", VoucherStatus.ACTIVE)).thenReturn(Optional.of(testVoucher));

        // When
        ApplyVoucherResponse result = orderService.applyVoucherToOrder(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.00"), result.getNewOrderPrice());
        assertEquals(new BigDecimal("15.00"), result.getDiscountOrderPrice());
    }

    @Test
    void applyVoucherToOrder_Success_EmptyVoucherList() {
        // Given
        ApplyVoucherRequest request = ApplyVoucherRequest.builder()
                .listCode(new ArrayList<>())
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .build();

        // When
        ApplyVoucherResponse result = orderService.applyVoucherToOrder(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getNewOrderPrice());
        assertEquals(new BigDecimal("10.00"), result.getNewShippingFee());
        assertEquals(BigDecimal.ZERO, result.getDiscountOrderPrice());
        assertEquals(BigDecimal.ZERO, result.getDiscountShippingPrice());
    }

    @Test
    void getOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
    }

    @Test
    void getOrderById_ThrowsException_OrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.getOrderById(1L));
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateOrderStatus_Success() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        orderService.updateOrderStatus(testOrder, OrderStatus.PENDING);

        // Then
        assertEquals(OrderStatus.PENDING, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void getUserOrderByStatus_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.getOrderByUserIdAndStatusOrderByIdDesc(1L, OrderStatus.PENDING)).thenReturn(orders);
        when(reviewRepository.existsByOrder(testOrder)).thenReturn(false);

        // When
        List<OrderResponse> result = orderService.getUserOrderByStatus(1L, OrderStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void getUserOrder_Success() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.getOrderByUserIdOrderByIdDesc(1L)).thenReturn(orders);
        when(reviewRepository.existsByOrder(testOrder)).thenReturn(false);

        // When
        List<OrderResponse> result = orderService.getUserOrder(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void listAllOrdersOfRestaurant_Success() {
        // Given
        List<Long> orderIds = Arrays.asList(1L, 2L);
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.getAllOrdersOfRestaurant(1L)).thenReturn(orderIds);
        when(orderRepository.findAllById(orderIds)).thenReturn(orders);

        // When
        List<Order> result = orderService.listAllOrdersOfRestaurant(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteOrderFailedPayment_Success() {
        // Given
        OrderVoucher orderVoucher = OrderVoucher.builder()
                .voucherDetail(testVoucherDetail)
                .build();
        orderVoucher.setId(1L);
        testOrder.setOrderVoucherList(Arrays.asList(orderVoucher));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        orderService.DeleteOrderFailedPayment(1L);

        // Then
        verify(cartDetailRepository).save(any(CartDetail.class));
        verify(voucherDetailRepository).save(any(VoucherDetail.class));
        verify(orderVoucherRepository).deleteById(1L);
        verify(orderRepository).delete(testOrder);
    }

    @Test
    void deleteOrderFailedPayment_ThrowsException_OrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.DeleteOrderFailedPayment(1L));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void reorder_Success() {
        // Given
        testOrder.setStatus(OrderStatus.COMPLETED);
        testUser.setId(1L);
        testOrder.setUser(testUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(foodService.getAdditionalFoodsOfRestaurant(eq(1L), eq(true), eq(0), eq(100)))
                .thenReturn(PageResponse.<List<GetFoodResponse>>builder()
                        .items(List.of(
                                GetFoodResponse.builder().id(2L).build(),
                                GetFoodResponse.builder().id(3L).build()
                                )
                        ).build());

        // When
        boolean result = orderService.reorder(1L, 1L);

        // Then
        assertTrue(result);
        //verify(cartService).clearCart(testCart);
        verify(cartDetailRepository).save(any(CartDetail.class));
        verify(cartRepository).save(testCart);
    }

    @Test
    void reorder_ThrowsException_OrderNotBelongToCustomer() {
        // Given
        testUser.setId(2L); // Different user ID
        testOrder.setUser(testUser);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.reorder(1L, 1L));
        assertEquals(ErrorCode.ORDER_NOT_BELONG_TO_CUSTOMER, exception.getErrorCode());
    }

    @Test
    void reorder_ThrowsException_OrderNotEligible() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        testUser.setId(1L);
        testOrder.setUser(testUser);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.reorder(1L, 1L));
        assertEquals(ErrorCode.ORDER_NOT_ELIGIBLE_FOR_REORDER, exception.getErrorCode());
    }

    @Test
    void checkDistanceOrder_Success() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(Arrays.asList(testCartDetail));

        LocationDistanceResponse locationResponse = LocationDistanceResponse.builder()
                .distance(30000.0)
                .duration(25.0)
                .shippingFee(new BigDecimal("15.00"))
                .build();
        when(locationService.getDistance(10.0, 20.0, 10.0, 20.0)).thenReturn(locationResponse);

        // When
        CheckDistanceResponse result = orderService.checkDistanceOrder(1L, 10.0, 20.0);

        // Then
        assertNotNull(result);
        assertTrue(result.isCheck());
        assertEquals(30000.0, result.getDistance());
        assertEquals(25.0, result.getDuration());
        assertEquals(new BigDecimal("15.00"), result.getShippingFee());
    }

    @Test
    void checkDistanceOrder_EmptyCart() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());

        // When
        CheckDistanceResponse result = orderService.checkDistanceOrder(1L, 10.0, 20.0);

        // Then
        assertNotNull(result);
        assertFalse(result.isCheck());
        assertEquals(-1.0, result.getDistance());
        assertEquals(-1.0, result.getDuration());
        assertEquals(BigDecimal.ZERO, result.getShippingFee());
    }

    @Test
    void getOrderAdmin_Success() {
        // Given
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(testOrder));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);
        when(foodService.getFoodPriceIn(anyLong(), any(LocalDateTime.class))).thenReturn(new BigDecimal("50.00"));
        when(foodService.getFood(anyLong(), eq(true))).thenReturn(GetFoodResponse.builder()
                .id(1L)
                .name("Additional Food")
                .build());

        // When
        PageResponse<List<OrderResponse>> result = orderService.getOrderAdmin(0, 10);

        // Then
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(0, result.getPage());
        assertEquals(1L, result.getTotal());
    }

    @Test
    void cancelOrder_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        orderService.cancelOrder(1L);

        // Then
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void cancelOrder_ThrowsException_OrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.cancelOrder(1L));
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getCartDetailsByOrder_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        List<CartDetail> result = orderService.getCartDetailsByOrder(1L, "PENDING");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCartDetail, result.get(0));
    }

    @Test
    void getOrdersByUser_Success() {
        // Given
        testUser.setOrders(Arrays.asList(testOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        List<Order> result = orderService.getOrdersByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
    }

    @Test
    void getOrdersByUser_ThrowsException_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> orderService.getOrdersByUser(1L));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}