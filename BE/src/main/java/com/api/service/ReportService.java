package com.api.service;

import com.api.dto.response.RevenueStatsDTO;

import java.util.Date;
import java.util.List;

public interface ReportService {
    List<RevenueStatsDTO> getRevenueStats(Integer restaurantId, Date dateFrom, Date dateTo, String groupBy);
    List<RevenueStatsDTO> getMonthlyRevenue(Integer restaurantId, int year);
}
