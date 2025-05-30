package com.api.service.Imp;

import com.api.dto.response.DashboardStatsResponse;
import com.api.dto.response.ShipperOrderResponse;
import com.api.entity.Order;
import com.api.entity.Shipper;
import com.api.repository.OrderRepository;
import com.api.service.DashboardService;
import com.api.service.OrderService;
import com.api.service.ShipperService;
import com.api.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImp implements DashboardService {

    private final OrderRepository orderRepository;
    private final ShipperService shipperService;
    private final OrderService orderService;

    @Override
    public DashboardStatsResponse getDashboardStats(String shipperPhone) {
        try {
            // Get shipper info
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Get today's date range
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

            // Get all orders for this shipper
            List<Order> allOrders = orderRepository.findAllByShipperId(shipper.getId());

            // Filter today's orders
            List<Order> todayOrders = allOrders.stream()
                    .filter(order -> order.getOrderDate().isAfter(startOfDay) &&
                            order.getOrderDate().isBefore(endOfDay))
                    .toList();

            // Calculate earnings
            EarningsCalculation earnings = calculateEarnings(allOrders, today);

            // Calculate order statistics
            OrderStatistics orderStats = calculateOrderStats(todayOrders, allOrders);

            // Calculate performance metrics
            PerformanceMetrics performance = calculatePerformance(shipper, allOrders);

            // Calculate activity stats
            ActivityStats activity = calculateActivityStats(todayOrders);

            return DashboardStatsResponse.builder()
                    // Earnings
                    .todayEarnings(earnings.todayEarnings)
                    .weeklyEarnings(earnings.weeklyEarnings)
                    .monthlyEarnings(earnings.monthlyEarnings)
                    .bonusEarnings(earnings.bonusEarnings)
                    .totalTips(earnings.totalTips)

                    // Order stats
                    .todayOrders(orderStats.todayTotal)
                    .completedOrders(orderStats.completed)
                    .cancelledOrders(orderStats.cancelled)
                    .pendingOrders(orderStats.pending)

                    // Performance
                    .averageRating(performance.averageRating)
                    .completionRate(performance.completionRate)
                    .acceptanceRate(performance.acceptanceRate)

                    // Activity
                    .totalDistanceToday(activity.totalDistance)
                    .totalDeliveryTime(activity.totalDeliveryTime)
                    .activeHours(activity.activeHours)
                    .gemsEarned(activity.gemsEarned)

                    // Status
                    .isOnline(shipper.getIsOnline())
                    .lastActiveTime(LocalDateTime.now())
                    .currentLocation(getCurrentLocationText(shipper))

                    // Insights
                    .bestPerformanceTime(getBestPerformanceTime(todayOrders))
                    .avgOrderValue(calculateAvgOrderValue(todayOrders))
                    .nearbyOrdersCount(0) // Will be calculated separately
                    .build();

        } catch (Exception e) {
            log.error("Error calculating dashboard stats for shipper {}", shipperPhone, e);
            throw e;
        }
    }

    @Override
    public Page<ShipperOrderResponse> getNearbyOrders(String shipperPhone, double latitude,
            double longitude, double radiusKm, int page, int size) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Create pageable WITHOUT sort (vì native query conflict)
            Pageable pageable = PageRequest.of(page, size);

            // Get nearby available orders using repository method
            Page<Order> nearbyOrders = orderRepository.findNearbyAvailableOrders(
                    latitude, longitude, radiusKm, pageable);

            // Convert to response
            return nearbyOrders.map(order -> {
                try {
                    ShipperOrderResponse response = convertToShipperOrderResponse(order);

                    // Calculate and set distance for this order
                    if (order.getDeliveryLatitude() != null && order.getDeliveryLongitude() != null) {
                        float distance = calculateDistance(latitude, longitude,
                                order.getDeliveryLatitude(), order.getDeliveryLongitude());
                        response.setDistance(distance);
                    }

                    return response;
                } catch (Exception e) {
                    log.warn("Error converting order {} to response", order.getId(), e);
                    return null;
                }
            });

        } catch (Exception e) {
            log.error("Error getting nearby orders for shipper {} at location ({}, {})",
                    shipperPhone, latitude, longitude, e);
            throw e;
        }
    }

    // ===== HELPER METHODS =====

    private EarningsCalculation calculateEarnings(List<Order> allOrders, LocalDate today) {
        LocalDateTime startOfWeek = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).atStartOfDay();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        BigDecimal todayEarnings = allOrders.stream()
                .filter(order -> order.getOrderDate().isAfter(startOfDay) &&
                        order.getOrderDate().isBefore(endOfDay) &&
                        order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> order.getShipperEarning() != null ? order.getShipperEarning() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal weeklyEarnings = allOrders.stream()
                .filter(order -> order.getOrderDate().isAfter(startOfWeek) &&
                        order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> order.getShipperEarning() != null ? order.getShipperEarning() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyEarnings = allOrders.stream()
                .filter(order -> order.getOrderDate().isAfter(startOfMonth) &&
                        order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> order.getShipperEarning() != null ? order.getShipperEarning() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalTips = allOrders.stream()
                .filter(order -> order.getOrderDate().isAfter(startOfDay) &&
                        order.getOrderDate().isBefore(endOfDay))
                .map(order -> order.getTip() != null ? order.getTip() : 0L)
                .reduce(0L, Long::sum);

        return new EarningsCalculation(todayEarnings, weeklyEarnings, monthlyEarnings,
                BigDecimal.ZERO, totalTips);
    }

    private OrderStatistics calculateOrderStats(List<Order> todayOrders, List<Order> allOrders) {
        int todayTotal = todayOrders.size();
        int completed = (int) todayOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        int cancelled = (int) todayOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        int pending = (int) todayOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PROCESSING ||
                        o.getStatus() == OrderStatus.SHIPPING)
                .count();

        return new OrderStatistics(todayTotal, completed, cancelled, pending);
    }

    private PerformanceMetrics calculatePerformance(Shipper shipper, List<Order> allOrders) {
        float averageRating = shipper.getRating() != null ? shipper.getRating().floatValue() : 0.0f;

        long completedCount = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();

        float completionRate = allOrders.isEmpty() ? 0.0f : (float) completedCount / allOrders.size() * 100;

        float acceptanceRate = shipper.getAcceptanceRate() != null ? shipper.getAcceptanceRate() : 0.0f;

        return new PerformanceMetrics(averageRating, completionRate, acceptanceRate);
    }

    private ActivityStats calculateActivityStats(List<Order> todayOrders) {
        float totalDistance = todayOrders.stream()
                .map(order -> order.getDistance() != null ? order.getDistance() : 0.0f)
                .reduce(0.0f, Float::sum) / 1000; // Convert to km

        int totalDeliveryTime = todayOrders.stream()
                .map(order -> order.getEstimatedTime() != null ? order.getEstimatedTime() : 0)
                .reduce(0, Integer::sum);

        int gemsEarned = todayOrders.stream()
                .map(order -> order.getGemsEarned() != null ? order.getGemsEarned() : 0)
                .reduce(0, Integer::sum);

        return new ActivityStats(totalDistance, totalDeliveryTime, 8, gemsEarned); // Assume 8 active hours
    }

    private String getCurrentLocationText(Shipper shipper) {
        if (shipper.getCurrentLatitude() != null && shipper.getCurrentLongitude() != null) {
            return String.format("%.4f, %.4f", shipper.getCurrentLatitude(), shipper.getCurrentLongitude());
        }
        return "Không xác định";
    }

    private String getBestPerformanceTime(List<Order> todayOrders) {
        // Simple implementation - return peak hours
        return "11:00 - 13:00"; // Lunch time peak
    }

    private float calculateDistance(double lat1, double lon1, Double lat2, Double lon2) {
        if (lat2 == null || lon2 == null)
            return 0.0f;

        // Haversine formula to calculate distance between two coordinates
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km

        return (float) (distance * 1000); // Convert to meters
    }

    private BigDecimal calculateAvgOrderValue(List<Order> todayOrders) {
        if (todayOrders.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal total = todayOrders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(todayOrders.size()), 2, RoundingMode.HALF_UP);
    }

    private ShipperOrderResponse convertToShipperOrderResponse(Order order) {
        // This is a simplified version - ideally should reuse from OrderServiceImp
        return ShipperOrderResponse.builder()
                .id(order.getId())
                .customerName(order.getUser().getName())
                .customerPhone(order.getUser().getPhone())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .distance(order.getDistance())
                .estimatedTime(order.getEstimatedTime())
                .build();
    }

    // Inner classes for calculation results
    private record EarningsCalculation(BigDecimal todayEarnings, BigDecimal weeklyEarnings,
            BigDecimal monthlyEarnings, BigDecimal bonusEarnings, Long totalTips) {
    }

    private record OrderStatistics(int todayTotal, int completed, int cancelled, int pending) {
    }

    private record PerformanceMetrics(float averageRating, float completionRate, float acceptanceRate) {
    }

    private record ActivityStats(float totalDistance, int totalDeliveryTime, int activeHours, int gemsEarned) {
    }
}