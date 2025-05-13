package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.NotificationResponse;
import com.api.entity.Restaurant;
import com.api.service.NotificationService;
import com.api.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final RestaurantService restaurantService;

    @GetMapping("/restaurant/{restaurantId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsOfRestaurant(@PathVariable long restaurantId) {
        log.info("Fetching notifications for restaurant {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(200)
                .message("fetching notifications of restaurant")
                .data(notificationService.fetchNotificationsPopup(restaurant.getAccount()))
                .build();
    }

    @PatchMapping("/{anId}/read")
    public ApiResponse<?> restaurantMarkAsRead(@PathVariable long anId) {
        log.info("Marking notification read for restaurant {}", anId);
        notificationService.markAsRead(anId);
        return ApiResponse.builder()
                .message("marking notification read for a-notification " + anId)
                .code(200)
                .build();
    }

    @PatchMapping("/restaurant/{restaurantId}/read-all")
    public ApiResponse<?> restaurantMarkAsReadAll(@PathVariable long restaurantId) {
        log.info("Marking notification all read for restaurant {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        notificationService.markAllAsRead(restaurant.getAccount());
        return ApiResponse.builder()
                .message("marking notification all read for restaurant" + restaurantId)
                .code(200)
                .build();
    }

    @DeleteMapping("/{anId}")
    public ApiResponse<?> restaurantMarkAsDelete(@PathVariable long anId) {
        log.info("Marking notification read for restaurant {}", anId);
        notificationService.markDeleted(anId);
        return ApiResponse.builder()
                .message("marking notification deleted for a-notification " + anId)
                .code(200)
                .build();
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public ApiResponse<?> restaurantMarkAsDeleteAll(@PathVariable long restaurantId) {
        log.info("Marking notification all deleted for restaurant {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        notificationService.markAllAsDeleted(restaurant.getAccount());
        return ApiResponse.builder()
                .message("marking notification all deleted for restaurant" + restaurantId)
                .code(200)
                .build();
    }
}
