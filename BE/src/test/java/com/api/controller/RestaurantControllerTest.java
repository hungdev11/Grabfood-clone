package com.api.controller;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.UpdateRestaurantRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.service.RestaurantService;
import com.api.utils.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
        objectMapper = new ObjectMapper();
        // Set the radiusKm field value for testing
        ReflectionTestUtils.setField(restaurantController, "radiusKm", 5.0);
    }

    @Test
    void addNewRestaurant_Success() throws Exception {
        // Given
        AddRestaurantRequest request = new AddRestaurantRequest();
        request.setName("Restaurant1");
        Long expectedId = 1L;

        when(restaurantService.addRestaurant(any(AddRestaurantRequest.class))).thenReturn(expectedId);

        // When & Then
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(expectedId));

        verify(restaurantService).addRestaurant(argThat(
                req -> req.getName().equals("Restaurant1")
        ));
    }

    @Test
    void addNewRestaurant_ServiceThrowsException() throws Exception {
        // Given
        AddRestaurantRequest request = new AddRestaurantRequest();
        request.setName("Restaurant1");
        when(restaurantService.addRestaurant(any(AddRestaurantRequest.class))).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(restaurantService).addRestaurant(argThat(req -> req.getName().equals("Restaurant1")));
    }

    @Test
    void getRestaurantById_Success() throws Exception {
        // Given
        long restaurantId = 1L;
        double userLat = 10.7769;
        double userLon = 106.7009;
        RestaurantResponse expectedResponse = new RestaurantResponse();

        when(restaurantService.getRestaurantResponse(restaurantId, userLat, userLon))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLon", String.valueOf(userLon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getRestaurantResponse(restaurantId, userLat, userLon);
    }

    @Test
    void getRestaurantById_WithDefaultCoordinates() throws Exception {
        // Given
        long restaurantId = 1L;
        RestaurantResponse expectedResponse = new RestaurantResponse();

        when(restaurantService.getRestaurantResponse(restaurantId, -1.0, -1.0))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getRestaurantResponse(restaurantId, -1.0, -1.0);
    }

    @Test
    void getRestaurants_Success() throws Exception {
        // Given
        String sortBy = "rating";
        double userLat = 10.7769;
        double userLon = 106.7009;
        List<RestaurantResponse> expectedResponse = Arrays.asList(new RestaurantResponse());

        when(restaurantService.getRestaurants(sortBy, userLat, userLon))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/restaurants")
                        .param("sortBy", sortBy)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLon", String.valueOf(userLon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getRestaurants(sortBy, userLat, userLon);
    }

    @Test
    void getRestaurants_WithDefaultParameters() throws Exception {
        // Given
        List<RestaurantResponse> expectedResponse = Arrays.asList(new RestaurantResponse());

        when(restaurantService.getRestaurants("name", -1.0, -1.0))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getRestaurants("name", -1.0, -1.0);
    }

    @Test
    void getRestaurantsNearBy_Success() throws Exception {
        // Given
        double userLat = 10.7769;
        double userLon = 106.7009;
        double radiusKm = 5.0;
        List<RestaurantResponse> expectedResponse = Arrays.asList(new RestaurantResponse());

        when(restaurantService.getNearbyRestaurants(userLat, userLon, radiusKm))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/restaurants/nearby")
                        .param("userLat", String.valueOf(userLat))
                        .param("userLon", String.valueOf(userLon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getNearbyRestaurants(userLat, userLon, radiusKm);
    }

    @Test
    void handleOrder_Success() throws Exception {
        // Given
        long restaurantId = 1L;
        Long orderId = 2L;
        OrderStatus status = OrderStatus.PENDING;

        doNothing().when(restaurantService).handleOrder(restaurantId, orderId, status);

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/handle-order/{orderId}", restaurantId, orderId)
                        .param("status", status.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).handleOrder(restaurantId, orderId, status);
    }

    @Test
    void updateRestaurantInfo_Success() throws Exception {
        // Given
        long restaurantId = 1L;
        UpdateRestaurantRequest request = new UpdateRestaurantRequest();
        request.setName("Rest1");

        doNothing().when(restaurantService).updateRestaurantInfo(anyLong(), any(UpdateRestaurantRequest.class));

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).updateRestaurantInfo(anyLong(), argThat(req -> req.getName().equals("Rest1")));
    }

    @Test
    void approveRestaurant_Success() throws Exception {
        // Given
        long restaurantId = 1L;

        doNothing().when(restaurantService).approveRestaurant(restaurantId);

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/approve", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant approved successfully"));

        verify(restaurantService).approveRestaurant(restaurantId);
    }

    @Test
    void rejectRestaurant_Success() throws Exception {
        // Given
        long restaurantId = 1L;

        doNothing().when(restaurantService).rejectRestaurant(restaurantId);

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/reject", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant rejected successfully"));

        verify(restaurantService).rejectRestaurant(restaurantId);
    }

    @Test
    void setRestaurantInactive_Success() throws Exception {
        // Given
        long restaurantId = 1L;

        doNothing().when(restaurantService).setRestaurantStatus(restaurantId, "INACTIVE");

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/inactive", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant set to inactive successfully"));

        verify(restaurantService).setRestaurantStatus(restaurantId, "INACTIVE");
    }

    @Test
    void setRestaurantActive_Success() throws Exception {
        // Given
        long restaurantId = 1L;

        doNothing().when(restaurantService).setRestaurantStatus(restaurantId, "ACTIVE");

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/active", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Restaurant set to active successfully"));

        verify(restaurantService).setRestaurantStatus(restaurantId, "ACTIVE");
    }

    @Test
    void getPendingRestaurants_Success() throws Exception {
        // Given
        List<RestaurantResponse> pendingRestaurants = Arrays.asList(new RestaurantResponse());

        when(restaurantService.getPendingRestaurants()).thenReturn(pendingRestaurants);

        // When & Then
        mockMvc.perform(get("/restaurants/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getPendingRestaurants();
    }

    @Test
    void getAllRestaurants_Success() throws Exception {
        // Given
        List<RestaurantResponse> allRestaurants = Arrays.asList(new RestaurantResponse());

        when(restaurantService.getAllRestaurants()).thenReturn(allRestaurants);

        // When & Then
        mockMvc.perform(get("/restaurants/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(restaurantService).getAllRestaurants();
    }

    @Test
    void getRestaurantByUsername_Success() throws Exception {
        // Given
        String username = "testuser";
        Long restaurantId = 1L;

        when(restaurantService.getRestaurantByUsername(username)).thenReturn(restaurantId);

        // When & Then
        mockMvc.perform(get("/restaurants/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(restaurantId));

        verify(restaurantService).getRestaurantByUsername(username);
    }

    @Test
    void handleOrder_ServiceThrowsException() throws Exception {
        // Given
        long restaurantId = 1L;
        Long orderId = 2L;
        OrderStatus status = OrderStatus.PENDING;

        doThrow(new RuntimeException("Order handling failed"))
                .when(restaurantService).handleOrder(restaurantId, orderId, status);

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/handle-order/{orderId}", restaurantId, orderId)
                        .param("status", status.toString()))
                .andExpect(status().isOk());

        verify(restaurantService).handleOrder(restaurantId, orderId, status);
    }

    @Test
    void updateRestaurantInfo_ServiceThrowsException() throws Exception {
        // Given
        long restaurantId = 1L;
        UpdateRestaurantRequest request = new UpdateRestaurantRequest();
        request.setName("");
        doThrow(new RuntimeException("Update failed"))
                .when(restaurantService).updateRestaurantInfo(anyLong(), any(UpdateRestaurantRequest.class));

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(restaurantService).updateRestaurantInfo(anyLong(), argThat(req-> req.getName().equals("")));
    }

    @Test
    void approveRestaurant_ServiceThrowsException() throws Exception {
        // Given
        long restaurantId = 1L;

        doThrow(new RuntimeException("Approval failed"))
                .when(restaurantService).approveRestaurant(restaurantId);

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/approve", restaurantId))
                .andExpect(status().isOk());

        verify(restaurantService).approveRestaurant(restaurantId);
    }

    @Test
    void getRestaurantById_ServiceThrowsException() throws Exception {
        // Given
        long restaurantId = 1L;

        when(restaurantService.getRestaurantResponse(anyLong(), anyDouble(), anyDouble()))
                .thenThrow(new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ErrorCode.RESTAURANT_NOT_FOUND.getMessage()));

        verify(restaurantService).getRestaurantResponse(restaurantId, -1.0, -1.0);
    }

    @Test
    void getRestaurantsNearBy_MissingRequiredParameters() throws Exception {
        // When & Then
        mockMvc.perform(get("/restaurants/nearby"))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).getNearbyRestaurants(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void handleOrder_InvalidOrderStatus() throws Exception {
        // Given
        long restaurantId = 1L;
        Long orderId = 2L;

        // When & Then
        mockMvc.perform(put("/restaurants/{restaurantId}/handle-order/{orderId}", restaurantId, orderId)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(restaurantService, never()).handleOrder(anyLong(), anyLong(), any(OrderStatus.class));
    }
}