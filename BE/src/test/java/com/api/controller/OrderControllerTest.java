package com.api.controller;

import com.api.dto.request.ApplyVoucherRequest;
import com.api.dto.response.*;
import com.api.entity.CartDetail;
import com.api.entity.Order;
import com.api.exception.AppException;
import com.api.jwt.JwtService;
import com.api.service.OrderService;
import com.api.service.UserService;
import com.api.utils.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Order testOrder;
    private List<Order> testOrders;
    private List<CartDetail> testCartDetails;
    private List<OrderResponse> testOrderResponses;
    private ApplyVoucherRequest applyVoucherRequest;
    private ApplyVoucherResponse applyVoucherResponse;
    private CheckDistanceResponse checkDistanceResponse;
    private PageResponse<GetOrderGroupResponse> orderGroupResponse;
    private PageResponse<List<OrderResponse>> adminOrderResponse;

    @ControllerAdvice
    static class TestControllerAdvice {
        @ExceptionHandler(AppException.class)
        public ResponseEntity<String> handleAppException(AppException ex) {
            return ResponseEntity.status(ex.getErrorCode().getStatusCode()).body(ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new TestControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For handling LocalDateTime

        // Setup test data
        testOrder = Order.builder()
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .address("Test Address")
                .orderDate(LocalDateTime.now())
                .build();
        testOrder.setId(1L); // Set ID for the order

        testOrders = Arrays.asList(testOrder);

        CartDetail cartDetail = CartDetail.builder()
                .quantity(2)
                .note("Test note")
                .build();
        cartDetail.setId(1L); // Set ID explicitly
        testCartDetails = Arrays.asList(cartDetail);

        testOrderResponses = Arrays.asList(
                OrderResponse.builder()
                        .id(1L)
                        .status(OrderStatus.PENDING)
                        .totalPrice(new BigDecimal("100.00"))
                        .shippingFee(new BigDecimal("10.00"))
                        .build()
        );

        applyVoucherRequest = ApplyVoucherRequest.builder()
                .listCode(Arrays.asList("TEST123"))
                .totalPrice(new BigDecimal("100.00"))
                .shippingFee(new BigDecimal("10.00"))
                .build();

        applyVoucherResponse = ApplyVoucherResponse.builder()
                .newOrderPrice(new BigDecimal("80.00"))
                .discountOrderPrice(new BigDecimal("20.00"))
                .newShippingFee(new BigDecimal("10.00"))
                .discountShippingPrice(BigDecimal.ZERO)
                .build();

        checkDistanceResponse = CheckDistanceResponse.builder()
                .check(true)
                .distance(5000.0)
                .duration(15.0)
                .shippingFee(new BigDecimal("15.00"))
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("100.00"))
                .build();

        GetOrderGroupResponse getOrderGroupResponse = GetOrderGroupResponse.builder()
                .statusList(Arrays.asList("PENDING"))
                .orders(Arrays.asList(orderResponse))
                .build();

        orderGroupResponse = PageResponse.<GetOrderGroupResponse>builder()
                .items(getOrderGroupResponse)
                .page(0)
                .size(15)
                .total(1L)
                .build();

        adminOrderResponse = PageResponse.<List<OrderResponse>>builder()
                .items(testOrderResponses)
                .page(0)
                .size(10)
                .total(1L)
                .build();
    }

    @Test
    void getListOrders_ShouldReturnOrders() throws Exception {
        // Given
        Long userId = 1L;
        when(orderService.getOrdersByUser(userId)).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/order/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).getOrdersByUser(userId);
    }

    @Test
    void getListOrdersOfRestaurant_ShouldReturnOrders() throws Exception {
        // Given
        Long restaurantId = 1L;
        when(orderService.getRestaurantOrders(eq(restaurantId), eq(0), eq(15), isNull()))
                .thenReturn(orderGroupResponse);

        // When & Then
        mockMvc.perform(get("/order/restaurant/{restaurantId}", restaurantId)
                        .param("page", "0")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get orders of restaurant successfully"))
                .andExpect(jsonPath("$.data.items.orders[0].id").value(1))
                .andExpect(jsonPath("$.data.items.statusList[0]").value("PENDING"))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(orderService).getRestaurantOrders(restaurantId, 0, 15, null);
    }

    @Test
    void getListCartDetails_ShouldReturnCartDetails() throws Exception {
        // Given
        Long orderId = 1L;
        when(orderService.getCartDetailsByOrder(orderId, "PENDING")).thenReturn(testCartDetails);

        // When & Then
        mockMvc.perform(get("/order/{orderId}/cart-details", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2));

        verify(orderService).getCartDetailsByOrder(orderId, "PENDING");
    }

    @Test
    void getUserOrderByStatus_ShouldReturnFilteredOrders() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validToken";
        String username = "user@example.com";

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.getUserIdByPhoneOrEmail(username)).thenReturn(userId);
        when(orderService.getUserOrderByStatus(userId, OrderStatus.PENDING)).thenReturn(testOrderResponses);

        // When & Then
        mockMvc.perform(get("/order")
                        .header("Authorization", "Bearer " + token)
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).getUserOrderByStatus(userId, OrderStatus.PENDING);
    }

    @Test
    void getUserOrder_ShouldReturnAllOrders() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validToken";
        String username = "user@example.com";

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.getUserIdByPhoneOrEmail(username)).thenReturn(userId);
        when(orderService.getUserOrder(userId)).thenReturn(testOrderResponses);

        // When & Then
        mockMvc.perform(get("/order")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(orderService).getUserOrder(userId);
    }

    @Test
    void getUserOrder_ShouldThrowException_WhenAuthorizationHeaderMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/order"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should apply voucher to order")
    void applyVoucherToOrder_ShouldReturnDiscountedPrice() throws Exception {
        // Given
        when(orderService.applyVoucherToOrder(any(ApplyVoucherRequest.class))).thenReturn(applyVoucherResponse);

        // When & Then
        mockMvc.perform(post("/order/check/applyVoucher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyVoucherRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.newOrderPrice").value(80.0))
                .andExpect(jsonPath("$.data.discountOrderPrice").value(20.0));

        verify(orderService).applyVoucherToOrder(any(ApplyVoucherRequest.class));
    }

    @Test
    void reorder_ShouldReturnSuccess() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        when(orderService.reorder(userId, orderId)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/order/user/{userId}/reorder/{orderId}", userId, orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(orderService).reorder(userId, orderId);
    }

    @Test
    void checkDistance_ShouldReturnDistanceInfo() throws Exception {
        // Given
        Long userId = 1L;
        double lat = 10.7769;
        double lon = 106.7009;
        when(orderService.checkDistanceOrder(userId, lat, lon)).thenReturn(checkDistanceResponse);

        // When & Then
        mockMvc.perform(get("/order/checkDistance")
                        .param("userId", String.valueOf(userId))
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.check").value(true))
                .andExpect(jsonPath("$.data.distance").value(5000.0))
                .andExpect(jsonPath("$.data.shippingFee").value(15.0));

        verify(orderService).checkDistanceOrder(userId, lat, lon);
    }

    @Test
    void getOrderAdmin_ShouldReturnPagedOrders() throws Exception {
        // Given
        when(orderService.getOrderAdmin(0, 10)).thenReturn(adminOrderResponse);

        // When & Then
        mockMvc.perform(get("/order/admin")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(orderService).getOrderAdmin(0, 10);
    }

    @Test
    void cancelOrder_ShouldReturnSuccess() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(put("/order/admin/cancel/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(orderService).cancelOrder(orderId);
    }
}