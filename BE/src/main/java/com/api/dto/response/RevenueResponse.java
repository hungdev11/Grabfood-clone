package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private List<MonthlyRevenue> monthlyRevenues;
    private BigDecimal totalRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenue {
        private String monthYear; // Format: "MM/YYYY"
        private BigDecimal amount;
    }
}
