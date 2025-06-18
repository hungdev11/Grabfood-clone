package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.NotificationResponse;
import com.api.entity.Account;
import com.api.entity.Restaurant;
import com.api.entity.User;
import com.api.service.NotificationService;
import com.api.service.RestaurantService;
import com.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Account account;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();
        account = new Account(); // Giả lập account
    }

    @Test
    void getNotificationsOfRestaurant_Success() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant();
        restaurant.setAccount(account);
        List<NotificationResponse> notifications = Collections.singletonList(new NotificationResponse());

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(notificationService.fetchNotificationsPopup(account)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/restaurant/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(restaurantService).getRestaurant(restaurantId);
        verify(notificationService).fetchNotificationsPopup(account);
    }

    @Test
    void getNotificationsOfClient_Success() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setAccount(account);
        List<NotificationResponse> notifications = Collections.singletonList(new NotificationResponse());

        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationService.fetchNotificationsPopup(account)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(userService).getUserById(userId);
        verify(notificationService).fetchNotificationsPopup(account);
    }

    @Test
    void restaurantMarkAsRead_Success() throws Exception {
        Long anId = 1L;

        doNothing().when(notificationService).markAsRead(anId);

        mockMvc.perform(patch("/notifications/{anId}/read", anId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification read for a-notification " + anId));

        verify(notificationService).markAsRead(anId);
    }

    @Test
    void restaurantMarkAsReadAll_Success() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant();
        restaurant.setAccount(account);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        doNothing().when(notificationService).markAllAsRead(account);

        mockMvc.perform(patch("/notifications/restaurant/{restaurantId}/read-all", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification all read for restaurant" + restaurantId));

        verify(restaurantService).getRestaurant(restaurantId);
        verify(notificationService).markAllAsRead(account);
    }

    @Test
    void restaurantMarkAsDelete_Success() throws Exception {
        Long anId = 1L;

        doNothing().when(notificationService).markDeleted(anId);

        mockMvc.perform(delete("/notifications/{anId}", anId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification deleted for a-notification " + anId));

        verify(notificationService).markDeleted(anId);
    }

    @Test
    void restaurantMarkAsDeleteAll_Success() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant();
        restaurant.setAccount(account);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        doNothing().when(notificationService).markAllAsDeleted(account);

        mockMvc.perform(delete("/notifications/restaurant/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification all deleted for restaurant" + restaurantId));

        verify(restaurantService).getRestaurant(restaurantId);
        verify(notificationService).markAllAsDeleted(account);
    }

    @Test
    void clientMarkAsReadAll_Success() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setAccount(account);

        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(notificationService).markAllAsRead(account);

        mockMvc.perform(patch("/notifications/user/{userId}/read-all", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification all read for restaurant" + userId));

        verify(userService).getUserById(userId);
        verify(notificationService).markAllAsRead(account);
    }

    @Test
    void clientMarkAsDeleteAll_Success() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setAccount(account);

        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(notificationService).markAllAsDeleted(account);

        mockMvc.perform(delete("/notifications/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("marking notification all deleted for user" + userId));

        verify(userService).getUserById(userId);
        verify(notificationService).markAllAsDeleted(account);
    }
}
