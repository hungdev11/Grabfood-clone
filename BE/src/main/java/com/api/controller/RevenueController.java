package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.RevenueResponse;
import com.api.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/revenues")
public class RevenueController {
    private final RevenueService revenueService;

    @GetMapping
    public ApiResponse<RevenueResponse> getRevenue(
            @RequestParam @DateTimeFormat(pattern = "MM/yyyy") YearMonth startMonthYear,
            @RequestParam @DateTimeFormat(pattern = "MM/yyyy") YearMonth endMonthYear) {

        log.info("Calculating revenue from {} to {}", startMonthYear, endMonthYear);

        return ApiResponse.<RevenueResponse>builder()
                .code(200)
                .message("Success")
                .data(revenueService.calculateRevenue(startMonthYear, endMonthYear))
                .build();
    }
}
