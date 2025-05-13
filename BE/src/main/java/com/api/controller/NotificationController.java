package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.NotificationResponse;
import com.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/restaurant/{restaurantId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsOfRestaurant(@PathVariable long restaurantId) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(200)
                .message("fetching notifications of restaurant")
                .data(notificationService.fetchNotificationsPopup(restaurantId))
                .build();
    }
}
