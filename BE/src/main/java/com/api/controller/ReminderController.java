package com.api.controller;

import com.api.dto.request.ReminderRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.ReminderResponse;
import com.api.entity.Reminder;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.service.ReminderService;
import com.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@Slf4j
public class ReminderController {
    private final ReminderService reminderService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<ReminderResponse> createReminder(HttpServletRequest httpServletRequest,
                                                        @RequestBody ReminderRequest request) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Long userId = userService.getUserIdByPhoneOrEmail(username);
        log.info("Creating reminder for user: {}", userId);
        ReminderResponse reminder = reminderService.createReminder(userId, request);

        return ApiResponse.<ReminderResponse>builder()
                .code(200)
                .message("Reminder created successfully")
                .data(reminder)
                .build();
    }

    @GetMapping
    public ApiResponse<List<ReminderResponse>> getUserReminders(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Long userId = userService.getUserIdByPhoneOrEmail(username);
        log.info("Getting reminders for user: {}", userId);
        List<ReminderResponse> reminders = reminderService.getUserReminders(userId);

        return ApiResponse.<List<ReminderResponse>>builder()
                .code(200)
                .message("Reminders retrieved successfully")
                .data(reminders)
                .build();
    }
    @DeleteMapping("/{reminderId}")
    public ApiResponse<Void> deleteReminder(@PathVariable Long reminderId) {

        reminderService.deleteReminder(reminderId);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Reminder deleted successfully")
                .build();
    }
    @DeleteMapping
    public ApiResponse<Void> deleteAllReminders(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Long userId = userService.getUserIdByPhoneOrEmail(username);
        log.info("Deleting all reminders for user: {}", userId);
        reminderService.deleteAllReminders(userId);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("All reminders deleted successfully")
                .build();
    }
}
