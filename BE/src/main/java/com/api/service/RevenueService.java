package com.api.service;

import com.api.dto.response.RevenueResponse;

import java.time.YearMonth;

public interface RevenueService {
    RevenueResponse calculateRevenue(YearMonth startMonthYear, YearMonth endMonthYear);
}
