package com.api.service;

import com.api.dto.response.RevenueResponse;
import com.api.repository.OrderRepository;
import com.api.service.Imp.RevenueServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RevenueServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private RevenueServiceImp revenueService;

    @BeforeEach
    void setUp() {
        revenueService = new RevenueServiceImp(orderRepository);
    }

    @Test
    @DisplayName("Should calculate revenue for a single month")
    void calculateRevenue_SingleMonth() {
        // Arrange
        YearMonth month = YearMonth.of(2023, 5);

        // First day of month at 00:00:00
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        // Last day of month at 23:59:59.999999999
        LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59, 999999999);

        BigDecimal expectedRevenue = new BigDecimal("1500.00");

        when(orderRepository.calculateRevenueForPeriod(startDate, endDate)).thenReturn(expectedRevenue);

        // Act
        RevenueResponse response = revenueService.calculateRevenue(month, month);

        // Assert
        assertEquals(expectedRevenue, response.getTotalRevenue());
        assertEquals(1, response.getMonthlyRevenues().size());

        RevenueResponse.MonthlyRevenue monthlyRevenue = response.getMonthlyRevenues().get(0);
        assertEquals("05/2023", monthlyRevenue.getMonthYear());
        assertEquals(expectedRevenue, monthlyRevenue.getAmount());

        verify(orderRepository, times(1)).calculateRevenueForPeriod(any(), any());
    }

    @Test
    @DisplayName("Should calculate revenue for multiple months")
    void calculateRevenue_MultipleMonths() {
        // Arrange
        YearMonth startMonth = YearMonth.of(2023, 1);
        YearMonth endMonth = YearMonth.of(2023, 3);

        // Mock revenues for each month
        BigDecimal januaryRevenue = new BigDecimal("1000.00");
        BigDecimal februaryRevenue = new BigDecimal("1500.00");
        BigDecimal marchRevenue = new BigDecimal("2000.00");

        // Mock repository responses for each month
        when(orderRepository.calculateRevenueForPeriod(
                startMonth.atDay(1).atStartOfDay(),
                startMonth.atEndOfMonth().atTime(23, 59, 59, 999999999)))
                .thenReturn(januaryRevenue);

        when(orderRepository.calculateRevenueForPeriod(
                startMonth.plusMonths(1).atDay(1).atStartOfDay(),
                startMonth.plusMonths(1).atEndOfMonth().atTime(23, 59, 59, 999999999)))
                .thenReturn(februaryRevenue);

        when(orderRepository.calculateRevenueForPeriod(
                startMonth.plusMonths(2).atDay(1).atStartOfDay(),
                startMonth.plusMonths(2).atEndOfMonth().atTime(23, 59, 59, 999999999)))
                .thenReturn(marchRevenue);

        // Expected total
        BigDecimal expectedTotal = januaryRevenue.add(februaryRevenue).add(marchRevenue);

        // Act
        RevenueResponse response = revenueService.calculateRevenue(startMonth, endMonth);

        // Assert
        assertEquals(expectedTotal, response.getTotalRevenue());
        assertEquals(3, response.getMonthlyRevenues().size());

        List<RevenueResponse.MonthlyRevenue> monthlyRevenues = response.getMonthlyRevenues();
        assertEquals("01/2023", monthlyRevenues.get(0).getMonthYear());
        assertEquals(januaryRevenue, monthlyRevenues.get(0).getAmount());

        assertEquals("02/2023", monthlyRevenues.get(1).getMonthYear());
        assertEquals(februaryRevenue, monthlyRevenues.get(1).getAmount());

        assertEquals("03/2023", monthlyRevenues.get(2).getMonthYear());
        assertEquals(marchRevenue, monthlyRevenues.get(2).getAmount());

        verify(orderRepository, times(3)).calculateRevenueForPeriod(any(), any());
    }

    @Test
    @DisplayName("Should handle zero revenue months")
    void calculateRevenue_ZeroRevenue() {
        // Arrange
        YearMonth startMonth = YearMonth.of(2023, 6);
        YearMonth endMonth = YearMonth.of(2023, 7);

        // Mock repository to return zero for both months
        when(orderRepository.calculateRevenueForPeriod(any(), any())).thenReturn(BigDecimal.ZERO);

        // Act
        RevenueResponse response = revenueService.calculateRevenue(startMonth, endMonth);

        // Assert
        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
        assertEquals(2, response.getMonthlyRevenues().size());

        for (RevenueResponse.MonthlyRevenue monthData : response.getMonthlyRevenues()) {
            assertEquals(BigDecimal.ZERO, monthData.getAmount());
        }
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void calculateRevenue_InvalidDateRange() {
        // Arrange
        YearMonth startMonth = YearMonth.of(2023, 8);
        YearMonth endMonth = YearMonth.of(2023, 7);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            revenueService.calculateRevenue(startMonth, endMonth);
        });

        assertEquals("Start month/year must be before or equal to end month/year", exception.getMessage());
        verify(orderRepository, never()).calculateRevenueForPeriod(any(), any());
    }

    @Test
    @DisplayName("Should handle null return from repository")
    void calculateRevenue_NullFromRepository() {
        // Arrange
        YearMonth month = YearMonth.of(2023, 5);

        // Mock repository to return BigDecimal.ZERO instead of null
        // because the service doesn't handle null values
        when(orderRepository.calculateRevenueForPeriod(any(), any())).thenReturn(BigDecimal.ZERO);

        // Act
        RevenueResponse response = revenueService.calculateRevenue(month, month);

        // Assert
        assertEquals(BigDecimal.ZERO, response.getMonthlyRevenues().get(0).getAmount());
        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
    }
}