package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.RevenueStatsDTO;
import com.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/revenue")
    public ApiResponse<List<RevenueStatsDTO>> getStats(
            @RequestParam Integer restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam String groupBy
    ) {
        List<RevenueStatsDTO> stats = reportService.getRevenueStats(
                restaurantId, java.sql.Date.valueOf(dateFrom), java.sql.Date.valueOf(dateTo), groupBy
        );
        return ApiResponse.<List<RevenueStatsDTO>>builder()
                .code(200)
                .message("Revenue report OK")
                .data(stats)
                .build();
    }

    @GetMapping("/monthly-revenue")
    public ApiResponse<List<RevenueStatsDTO>> getStats(
            @RequestParam Integer restaurantId,
            @RequestParam int year
    ) {
        List<RevenueStatsDTO> stats = reportService.getMonthlyRevenue(
                restaurantId, year
        );
        return ApiResponse.<List<RevenueStatsDTO>>builder()
                .code(200)
                .message("Revenue report OK")
                .data(stats)
                .build();
    }
}
