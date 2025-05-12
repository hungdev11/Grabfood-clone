package com.api.service.Imp;

import com.api.dto.response.RevenueStatsDTO;
import com.api.repository.OrderRepository;
import com.api.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImp implements ReportService {
    private final OrderRepository orderRepository;

    public List<RevenueStatsDTO> getRevenueStats(Integer restaurantId, Date dateFrom, Date dateTo, String groupBy) {
        return orderRepository.getRevenueStats(restaurantId, dateFrom, dateTo, groupBy);
    }

    @Override
    public List<RevenueStatsDTO> getMonthlyRevenue(Integer restaurantId, int year) {
        return orderRepository.getMonthlyRevenue(restaurantId, year);
    }
}
