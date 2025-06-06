package com.api.controller;

import com.api.dto.model.DriverDto;
import com.api.dto.request.OrderActionRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.DriverLoginResponse;
import com.api.entity.Shipper;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.service.DriverService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/drivers")
@RequiredArgsConstructor
@Slf4j
public class ManageDriverController {
    private final PasswordEncoder passwordEncoder;
    private final DriverService driverService;
    private final JwtService jwtService;

    @PutMapping("/status/{idDriver}")
    public ApiResponse<Void> updateDriverStatus(
            HttpServletRequest httpServletRequest,
            @RequestParam String newStatus, @PathVariable Long idDriver) {

        try {

            // Coordinate multiple services to change driver status
            driverService.updateDriverStatus(idDriver, newStatus);

            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Cập nhật trạng thái tài xế thành công")
                    .build();
        } catch (AppException e) {
            log.warn("Driver status update failed: {}", e.getMessage());
            return ApiResponse.<Void>builder()
                    .code(400)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<DriverDto>> getAllDrivers(HttpServletRequest request) {
        try {
            List<DriverDto> drivers = driverService.getAllDrivers();
            log.info(passwordEncoder.encode("123456"));
            return ApiResponse.<List<DriverDto>>builder()
                    .code(200)
                    .message("Lấy danh sách tài xế thành công")
                    .data(drivers)
                    .build();
        } catch (AppException e) {
            log.warn("Failed to get all drivers: {}", e.getMessage());
            return ApiResponse.<List<DriverDto>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error when getting all drivers", e);
            return ApiResponse.<List<DriverDto>>builder()
                    .code(500)
                    .message("Lỗi hệ thống khi lấy danh sách tài xế")
                    .build();
        }
    }
}
