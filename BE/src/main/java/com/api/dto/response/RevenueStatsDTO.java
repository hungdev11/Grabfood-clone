package com.api.dto.response;

public interface RevenueStatsDTO {
    String getLabel();
    Integer getTotalOrders();
    Double getGrossRevenue();
    Double getNetRevenue();
}

