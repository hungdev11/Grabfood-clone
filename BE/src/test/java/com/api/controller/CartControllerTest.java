package com.api.controller;

import com.api.dto.request.AddToCartRequest;
import com.api.dto.request.CartUpdateRequest;
import com.api.dto.request.DeleteCartItemRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.CartResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.exception.GlobalExceptionHandler;
import com.api.jwt.JwtService;
import com.api.service.CartService;
import com.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addToCart_Success() throws Exception {
        // Given
        Long userId = 1L;
        AddToCartRequest request = new AddToCartRequest();
        // Set properties for request as needed

        doNothing().when(cartService).addToCart(userId, request);

        // When & Then
        mockMvc.perform(post("/cart/add")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).addToCart(userId, request);
    }

    @Test
    void removeFromCart_Success() throws Exception {
        // Given
        Long cartId = 1L;
        Long foodId = 2L;
        List<Long> additionalItems = Arrays.asList(3L, 4L);

        doNothing().when(cartService).removeFromCart(cartId, foodId, additionalItems);

        // When & Then
        mockMvc.perform(delete("/cart/{cartId}/remove/{foodId}", cartId, foodId)
                        .param("additionalItems", "3", "4"))
                .andExpect(status().isNoContent());

        verify(cartService).removeFromCart(cartId, foodId, additionalItems);
    }

    @Test
    void getAllCartDetailUser_Success() {
        // Given
        String token = "valid-token";
        String username = "testuser";
        Long userId = 1L;
        CartResponse mockCartResponse = new CartResponse();

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.getUserIdByPhoneOrEmail(username)).thenReturn(userId);
        when(cartService.getAllCartDetailUser(userId)).thenReturn(mockCartResponse);

        // When
        ApiResponse<CartResponse> result = cartController.getAllCartDetailUser(httpServletRequest);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMessage());
        assertEquals(mockCartResponse, result.getData());

        verify(jwtService).extractUsername(token);
        verify(userService).getUserIdByPhoneOrEmail(username);
        verify(cartService).getAllCartDetailUser(userId);
    }

    @Test
    void getAllCartDetailUser_MissingAuthorizationHeader() {
        // Given
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartController.getAllCartDetailUser(httpServletRequest));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
        assertEquals("Authorization header is missing or invalid", exception.getMessage());

        verify(jwtService, never()).extractUsername(anyString());
        verify(userService, never()).getUserIdByPhoneOrEmail(anyString());
        verify(cartService, never()).getAllCartDetailUser(anyLong());
    }

    @Test
    void getAllCartDetailUser_InvalidAuthorizationHeader() {
        // Given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartController.getAllCartDetailUser(httpServletRequest));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
        assertEquals("Authorization header is missing or invalid", exception.getMessage());

        verify(jwtService, never()).extractUsername(anyString());
        verify(userService, never()).getUserIdByPhoneOrEmail(anyString());
        verify(cartService, never()).getAllCartDetailUser(anyLong());
    }

    @Test
    void getCartTest_Success() {
        // Given
        Long testUserId = 2L;
        CartResponse mockCartResponse = new CartResponse();
        when(cartService.getAllCartDetailUser(testUserId)).thenReturn(mockCartResponse);

        // When
        ApiResponse<CartResponse> result = cartController.getCartTest();

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMessage());
        assertEquals(mockCartResponse, result.getData());

        verify(cartService).getAllCartDetailUser(testUserId);
    }

    @Test
    void updateQuantity_Success() throws Exception {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();
        request.setUserId(1L);
        request.setNewQuantity(5);
        request.setFoodId(1L);
        // Set properties for request as needed

        doNothing().when(cartService).updateCartDetailQuantity(any(CartUpdateRequest.class));

        // When & Then
        mockMvc.perform(put("/cart/update-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).updateCartDetailQuantity(argThat(req ->
                req.getFoodId() == request.getFoodId() &&
                req.getNewQuantity() == request.getNewQuantity() &&
                Objects.equals(req.getUserId(), request.getUserId())
        ));
    }

    @Test
    void update_Success() throws Exception {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();
        request.setUserId(1L);
        request.setNewQuantity(5);
        request.setFoodId(1L);

        doNothing().when(cartService).updateCart(any(CartUpdateRequest.class));

        // When & Then
        mockMvc.perform(put("/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).updateCart(argThat(req ->
                req.getFoodId() == request.getFoodId() &&
                        req.getNewQuantity() == request.getNewQuantity() &&
                        Objects.equals(req.getUserId(), request.getUserId())
        ));    }

    @Test
    void deleteCartItem_Success() throws Exception {
        // Given
        DeleteCartItemRequest request = new DeleteCartItemRequest();
        request.setUserId(1L);
        request.setFoodId(2L);
        request.setAdditionalFoodIds(Arrays.asList(3L, 4L));

        doNothing().when(cartService).removeFromCart(
                request.getUserId(),
                request.getFoodId(),
                request.getAdditionalFoodIds()
        );

        // When & Then
        mockMvc.perform(delete("/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).removeFromCart(
                request.getUserId(),
                request.getFoodId(),
                request.getAdditionalFoodIds()
        );
    }

    @Test
    void deleteCartDetail_Success() throws Exception {
        // Given
        long cartDetailId = 1L;

        doNothing().when(cartService).deleteCartDetail(cartDetailId);

        // When & Then
        mockMvc.perform(delete("/cart")
                        .param("cartDetailId", String.valueOf(cartDetailId)))
                .andExpect(status().isOk());

        verify(cartService).deleteCartDetail(cartDetailId);
    }

    @Test
    void checkRestaurantOpen_Success() throws Exception {
        // Given
        long cartId = 1L;
        boolean expectedResult = true;

        when(cartService.checkRestaurantOpen(cartId)).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(get("/cart/checkOpen")
                        .param("cartId", String.valueOf(cartId)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(cartService).checkRestaurantOpen(cartId);
    }

    @Test
    void checkRestaurantOpen_ReturnsFalse() throws Exception {
        // Given
        long cartId = 1L;
        boolean expectedResult = false;

        when(cartService.checkRestaurantOpen(cartId)).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(get("/cart/checkOpen")
                        .param("cartId", String.valueOf(cartId)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(cartService).checkRestaurantOpen(cartId);
    }

    @Test
    void addToCart_ServiceThrowsException() throws Exception {
        // Given
        Long userId = 1L;
        AddToCartRequest request = new AddToCartRequest();

        doThrow(new AppException(ErrorCode.FOOD_NOT_FOUND)).when(cartService).addToCart(userId, request);

        // When & Then
        mockMvc.perform(post("/cart/add")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ErrorCode.FOOD_NOT_FOUND.getMessage()));

        verify(cartService).addToCart(userId, request);
    }

    @Test
    void updateQuantity_ServiceThrowsException() throws Exception {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();

        doThrow(new RuntimeException("Update failed")).when(cartService).updateCartDetailQuantity(request);

        // When & Then
        mockMvc.perform(put("/cart/update-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(cartService).updateCartDetailQuantity(request);
    }

    @Test
    void update_ServiceThrowsException() throws Exception {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();
        request.setFoodId(1L);

        doThrow(new AppException(ErrorCode.CART_ITEM_NOT_FOUND))
                .when(cartService).updateCart(any(CartUpdateRequest.class));

        // When & Then
        mockMvc.perform(put("/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ErrorCode.CART_ITEM_NOT_FOUND.getMessage()));

        verify(cartService).updateCart(argThat(req ->
                req.getFoodId() == request.getFoodId()
        ));
    }



    @Test
    void getAllCartDetailUser_JwtServiceThrowsException() {
        // Given
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> cartController.getAllCartDetailUser(httpServletRequest));

        verify(jwtService).extractUsername(token);
        verify(userService, never()).getUserIdByPhoneOrEmail(anyString());
        verify(cartService, never()).getAllCartDetailUser(anyLong());
    }

    @Test
    void getAllCartDetailUser_UserServiceThrowsException() {
        // Given
        String token = "valid-token";
        String username = "testuser";

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.getUserIdByPhoneOrEmail(username)).thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> cartController.getAllCartDetailUser(httpServletRequest));

        verify(jwtService).extractUsername(token);
        verify(userService).getUserIdByPhoneOrEmail(username);
        verify(cartService, never()).getAllCartDetailUser(anyLong());
    }

    @Test
    void checkRestaurantOpen_returnFalse() throws Exception {
        // Given
        long cartId = 1L;

        when(cartService.checkRestaurantOpen(cartId)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/cart/checkOpen")
                        .param("cartId", String.valueOf(cartId)))
                .andExpect(status().isOk());

        verify(cartService).checkRestaurantOpen(cartId);
    }
}