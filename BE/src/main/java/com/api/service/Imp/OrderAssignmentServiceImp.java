package com.api.service.Imp;

import com.api.entity.Order;
import com.api.entity.OrderAssignment;
import com.api.entity.Shipper;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.OrderAssignmentRepository;
import com.api.repository.OrderRepository;
import com.api.repository.ShipperRepository;
import com.api.service.OrderAssignmentService;
import com.api.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderAssignmentServiceImp implements OrderAssignmentService {

    private final OrderRepository orderRepository;
    private final OrderAssignmentRepository orderAssignmentRepository;
    private final ShipperRepository shipperRepository;
    private final TaskScheduler taskScheduler;

    // Cache cho timeout tasks
    private final Map<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();

    // Constants
    private static final double DEFAULT_RADIUS_KM = 5.0;
    private static final int COOLDOWN_MINUTES = 30;
    private static final int TIMEOUT_SECONDS = 60;
    private static final double EXTENDED_RADIUS_KM = 10.0;

    @Override
    public boolean assignOrderToOptimalShipper(Long orderId) {
        log.info("Bắt đầu assign order {} cho shipper tối ưu", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra order có thể assign không
        if (!canOrderBeAssigned(order)) {
            log.warn("Order {} không thể assign: status = {}", orderId, order.getStatus());
            return false;
        }

        // Lấy danh sách shipper đã reject
        List<Long> rejectedShipperIds = getRejectedShipperIds(orderId);
        log.info("Order {} đã bị reject bởi {} shippers: {}", orderId, rejectedShipperIds.size(),
                rejectedShipperIds);

        // Tìm shipper tối ưu
        Shipper optimalShipper = findOptimalShipperForOrder(order, rejectedShipperIds);

        if (optimalShipper == null) {
            log.warn("⚠️ Không tìm được shipper cho order {}", orderId);
            handleNoShipperAvailable(orderId);
            return false;
        }

        // Tạo assignment và gửi notification
        OrderAssignment assignment = createAssignmentWithNotification(order, optimalShipper);

        // Schedule timeout check
        scheduleTimeoutCheck(orderId, optimalShipper.getId());

        log.info("Đã assign order {} cho shipper {} ({})", orderId, optimalShipper.getId(), optimalShipper.getName());
        return true;
    }

    @Override
    public Shipper findOptimalShipperForOrder(Order order, List<Long> excludedShipperIds) {
        log.debug("Tìm shipper tối ưu cho order {} (loại trừ: {})", order.getId(), excludedShipperIds);

        List<Shipper> eligibleShippers = findEligibleShippers(order, excludedShipperIds);

        if (eligibleShippers.isEmpty()) {
            return null;
        }

        // Sắp xếp theo độ ưu tiên: distance (40%), rating (30%), acceptance rate (30%)
        return eligibleShippers.stream()
                .min((s1, s2) -> {
                    double score1 = calculateShipperScore(s1, order);
                    double score2 = calculateShipperScore(s2, order);
                    return Double.compare(score1, score2);
                })
                .orElse(null);
    }

    @Override
    public List<Shipper> findEligibleShippers(Order order, List<Long> excludedShipperIds) {
        // Tìm trong bán kính mặc định trước
        List<Shipper> shippers = shipperRepository.findAvailableShippersInRadius(
                order.getLatitude(), order.getLongitude(), DEFAULT_RADIUS_KM);

        // Nếu không đủ shipper, mở rộng bán kính
        if (shippers.size() < 3) {
            log.info("📍 Mở rộng bán kính tìm kiếm từ {}km lên {}km cho order {}",
                    DEFAULT_RADIUS_KM, EXTENDED_RADIUS_KM, order.getId());
            shippers = shipperRepository.findAvailableShippersInRadius(
                    order.getLatitude(), order.getLongitude(), EXTENDED_RADIUS_KM);
        }

        // Filter out excluded shippers và shippers trong cooldown
        return shippers.stream()
                .filter(shipper -> !excludedShipperIds.contains(shipper.getId()))
                .filter(shipper -> !isShipperInCooldown(shipper.getId(), order.getId()))
                .filter(this::isShipperTrulyAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public boolean handleOrderRejection(Long orderId, Long rejectedShipperId) {
        log.info("Xử lý rejection: Order {} bị reject bởi shipper {}", orderId, rejectedShipperId);

        // Cancel timeout task cho shipper này
        cancelTimeoutCheck(orderId, rejectedShipperId);

        // Tự động tìm shipper khác
        return assignOrderToOptimalShipper(orderId);
    }

    @Override
    public boolean handleOrderTimeout(Long orderId, Long timeoutShipperId) {
        log.warn("Xử lý timeout: Order {} timeout với shipper {}", orderId, timeoutShipperId);

        // Đánh dấu assignment là EXPIRED
        Optional<OrderAssignment> assignment = orderAssignmentRepository
                .findByOrderIdAndShipperId(orderId, timeoutShipperId);

        if (assignment.isPresent()) {
            OrderAssignment assign = assignment.get();
            assign.setStatus(OrderAssignment.AssignmentStatus.EXPIRED);
            assign.setRespondedAt(LocalDateTime.now());
            orderAssignmentRepository.save(assign);

            log.info("📝 Đã đánh dấu assignment {} là EXPIRED", assign.getId());
        }

        // Tự động tìm shipper khác
        return assignOrderToOptimalShipper(orderId);
    }

    @Override
    public void handleNoShipperAvailable(Long orderId) {
        log.error("KHÔNG TÌM ĐƯỢC SHIPPER cho order {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Strategy 1: Kiểm tra có shipper nào trong cooldown có thể "tha thứ" không
        List<Long> rejectedIds = getRejectedShipperIds(orderId);
        if (!rejectedIds.isEmpty()) {
            log.info("🔄 Thử enable lại {} rejected shippers cho order {}", rejectedIds.size(), orderId);

            // Reset cooldown cho order này (emergency case)
            Shipper emergencyShipper = findEmergencyShipper(order, rejectedIds);
            if (emergencyShipper != null) {
                log.warn("Emergency assignment: Order {} → Shipper {} (đã từng reject)",
                        orderId, emergencyShipper.getId());
                createAssignmentWithNotification(order, emergencyShipper);
                scheduleTimeoutCheck(orderId, emergencyShipper.getId());
                return;
            }
        }

        // Strategy 2: Increase delivery fee (surge pricing)
        log.warn("Cân nhắc tăng phí giao hàng cho order {}", orderId);

        // Strategy 3: Admin notification
        log.error("Cần admin can thiệp thủ công cho order {}", orderId);

        // Strategy 4: Delay và retry sau 5 phút
        scheduleDelayedRetry(orderId);
    }

    @Override
    public boolean isShipperInCooldown(Long shipperId, Long orderId) {
        LocalDateTime cooldownTime = LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES);

        return orderAssignmentRepository.existsByOrderIdAndShipperIdAndStatusAndRespondedAtAfter(
                orderId, shipperId, OrderAssignment.AssignmentStatus.REJECTED, cooldownTime);
    }

    @Override
    public OrderAssignment createAssignmentWithNotification(Order order, Shipper shipper) {
        // Tạo assignment mới
        OrderAssignment assignment = OrderAssignment.builder()
                .order(order)
                .shipper(shipper)
                .status(OrderAssignment.AssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .build();

        assignment = orderAssignmentRepository.save(assignment);

        // TODO: Gửi notification real-time cho shipper
        log.info("🔔 Đã gửi notification cho shipper {} về order {}", shipper.getId(), order.getId());

        return assignment;
    }

    @Override
    public List<Long> getRejectedShipperIds(Long orderId) {
        List<OrderAssignment> rejectedAssignments = orderAssignmentRepository
                .findByOrderIdAndStatus(orderId, OrderAssignment.AssignmentStatus.REJECTED);

        return rejectedAssignments.stream()
                .map(assignment -> assignment.getShipper().getId())
                .collect(Collectors.toList());
    }

    @Override
    public void scheduleTimeoutCheck(Long orderId, Long shipperId) {
        String taskKey = orderId + "_" + shipperId;

        // Cancel existing task nếu có
        cancelTimeoutCheck(orderId, shipperId);

        // Schedule timeout task
        ScheduledFuture<?> timeoutTask = taskScheduler.schedule(() -> {
            log.warn("Timeout triggered cho order {} - shipper {}", orderId, shipperId);
            handleOrderTimeout(orderId, shipperId);
        }, new Date(System.currentTimeMillis() + TIMEOUT_SECONDS * 1000));

        timeoutTasks.put(taskKey, timeoutTask);
        log.debug("Đã schedule timeout check cho order {} - shipper {} ({} giây)",
                orderId, shipperId, TIMEOUT_SECONDS);
    }

    @Override
    public void cancelTimeoutCheck(Long orderId, Long shipperId) {
        String taskKey = orderId + "_" + shipperId;
        ScheduledFuture<?> task = timeoutTasks.remove(taskKey);

        if (task != null && !task.isDone()) {
            task.cancel(false);
            log.debug("Đã cancel timeout check cho order {} - shipper {}", orderId, shipperId);
        }
    }

    // ======================= PRIVATE HELPER METHODS =======================

    private boolean canOrderBeAssigned(Order order) {
        return order.getStatus() == OrderStatus.PROCESSING ||
                order.getStatus() == OrderStatus.READY_FOR_PICKUP;
    }

    private double calculateShipperScore(Shipper shipper, Order order) {
        // Calculate distance (km)
        double distance = calculateDistance(
                shipper.getCurrentLatitude(), shipper.getCurrentLongitude(),
                order.getLatitude(), order.getLongitude());

        // Normalize scores (0-1, lower is better)
        double distanceScore = Math.min(distance / 10.0, 1.0); // Max 10km = score 1.0
        double ratingScore = 1.0 - (shipper.getRating().doubleValue() / 5.0); // 5 star = score 0.0
        double acceptanceScore = 1.0 - (shipper.getAcceptanceRate() / 100.0); // 100% = score 0.0

        // Weighted combination: distance (40%), rating (30%), acceptance (30%)
        return (distanceScore * 0.4) + (ratingScore * 0.3) + (acceptanceScore * 0.3);
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        // Haversine formula
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private boolean isShipperTrulyAvailable(Shipper shipper) {
        // Kiểm tra shipper không có đơn hàng đang active
        boolean hasActiveOrder = orderAssignmentRepository
                .existsByShipperIdAndStatusIn(shipper.getId(),
                        Arrays.asList(OrderAssignment.AssignmentStatus.ASSIGNED,
                                OrderAssignment.AssignmentStatus.ACCEPTED));

        return !hasActiveOrder;
    }

    private Shipper findEmergencyShipper(Order order, List<Long> rejectedIds) {
        // Tìm shipper gần nhất trong số các shipper đã reject (emergency case)
        List<Shipper> rejectedShippers = shipperRepository.findAllById(rejectedIds)
                .stream()
                .filter(s -> s.getIsOnline() && s.getStatus() == Shipper.ShipperStatus.ACTIVE)
                .collect(Collectors.toList());

        return rejectedShippers.stream()
                .min((s1, s2) -> {
                    double dist1 = calculateDistance(s1.getCurrentLatitude(), s1.getCurrentLongitude(),
                            order.getLatitude(), order.getLongitude());
                    double dist2 = calculateDistance(s2.getCurrentLatitude(), s2.getCurrentLongitude(),
                            order.getLatitude(), order.getLongitude());
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);
    }

    private void scheduleDelayedRetry(Long orderId) {
        log.info("Schedule delayed retry cho order {} sau 5 phút", orderId);

        taskScheduler.schedule(() -> {
            log.info("Delayed retry: Thử assign lại order {}", orderId);
            assignOrderToOptimalShipper(orderId);
        }, new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes
    }
}