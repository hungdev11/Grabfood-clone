package com.api.service.Imp;

import com.api.dto.response.RevenueResponse;
import com.api.repository.OrderRepository;
import com.api.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RevenueServiceImp implements RevenueService {
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    @Override
    public RevenueResponse calculateRevenue(YearMonth startMonthYear, YearMonth endMonthYear) {
        if (startMonthYear.isAfter(endMonthYear)) {
            throw new IllegalArgumentException("Start month/year must be before or equal to end month/year");
        }
        List<RevenueResponse.MonthlyRevenue> monthlyRevenues = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        YearMonth currentMonth = startMonthYear;
        while (!currentMonth.isAfter(endMonthYear)) {
            LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

            BigDecimal monthRevenue = orderRepository.calculateRevenueForPeriod(monthStart, monthEnd);

            RevenueResponse.MonthlyRevenue monthData = RevenueResponse.MonthlyRevenue.builder()
                    .monthYear(currentMonth.format(MONTH_YEAR_FORMATTER))
                    .amount(monthRevenue)
                    .build();

            monthlyRevenues.add(monthData);
            totalRevenue = totalRevenue.add(monthRevenue);

            currentMonth = currentMonth.plusMonths(1);
        }
        return RevenueResponse.builder()
                .monthlyRevenues(monthlyRevenues)
                .totalRevenue(totalRevenue)
                .build();
    }
}
