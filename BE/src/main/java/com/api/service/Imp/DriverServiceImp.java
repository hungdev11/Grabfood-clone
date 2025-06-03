package com.api.service.Imp;

import com.api.dto.request.*;
import com.api.dto.response.*;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.repository.*;
import com.api.service.DriverService;
import com.api.service.OrderAssignmentService;
import com.api.utils.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của DriverService
 * Xử lý tất cả logic nghiệp vụ cho Driver/Shipper
 * Sử dụng Redis cache để tối ưu hiệu suất
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImp implements DriverService {

    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;
    private final OrderAssignmentRepository orderAssignmentRepository;
    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RewardRepository rewardRepository;
    private final ShipperRewardRepository shipperRewardRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrderAssignmentService orderAssignmentService;

    // ===============================
    // AUTHENTICATION & PROFILE
    // ===============================

    @Override
    public DriverLoginResponse login(DriverLoginRequest request) {
        try {
            log.info("Đăng nhập cho shipper với phone: {}", request.getPhone());

            // Tìm shipper theo phone
            Shipper shipper = shipperRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                            "Không tìm thấy shipper với số điện thoại này"));

            // Kiểm tra trạng thái shipper
            if (shipper.getStatus() == Shipper.ShipperStatus.SUSPENDED) {
                throw new AppException(ErrorCode.ACCOUNT_LOCKED, "Tài khoản shipper đã bị tạm khóa");
            }

            if (shipper.getStatus() == Shipper.ShipperStatus.INACTIVE) {
                throw new AppException(ErrorCode.ACCOUNT_LOCKED, "Tài khoản shipper đã bị vô hiệu hóa");
            }

            // Lấy account để xác thực
            Account account = shipper.getAccount();
            if (account == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản liên kết");
            }

            // Xác thực mật khẩu
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account.getUsername(), request.getPassword()));

            if (!authentication.isAuthenticated()) {
                throw new AppException(ErrorCode.ACCOUNT_PASSWORD_NOT_MATCH, "Mật khẩu không chính xác");
            }

            // Tạo JWT token
            String token = jwtService.generateToken(account.getUsername());

            log.info("Đăng nhập thành công cho shipper ID: {}", shipper.getId());

            return DriverLoginResponse.builder()
                    .token(token)
                    .message("Đăng nhập thành công")
                    .shipperId(shipper.getId())
                    .name(shipper.getName())
                    .phone(shipper.getPhone())
                    .email(shipper.getEmail())
                    .rating(shipper.getRating())
                    .status(shipper.getStatus().name())
                    .isOnline(shipper.getIsOnline())
                    .vehicleType(shipper.getVehicleType())
                    .licensePlate(shipper.getLicensePlate())
                    .totalOrders(shipper.getTotalOrders())
                    .completedOrders(shipper.getCompletedOrders())
                    .acceptanceRate(shipper.getAcceptanceRate())
                    .gems(shipper.getGems())
                    .build();

        } catch (Exception e) {
            log.error("Lỗi đăng nhập shipper: {}", e.getMessage());
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException(ErrorCode.ACCOUNT_PASSWORD_NOT_MATCH, "Đăng nhập thất bại");
        }
    }

    @Override
    public DriverLoginResponse getShipperFromToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

            Shipper shipper = shipperRepository.findByAccountId(account.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy shipper"));

            return DriverLoginResponse.builder()
                    .shipperId(shipper.getId())
                    .name(shipper.getName())
                    .phone(shipper.getPhone())
                    .email(shipper.getEmail())
                    .rating(shipper.getRating())
                    .status(shipper.getStatus().name())
                    .isOnline(shipper.getIsOnline())
                    .vehicleType(shipper.getVehicleType())
                    .licensePlate(shipper.getLicensePlate())
                    .totalOrders(shipper.getTotalOrders())
                    .completedOrders(shipper.getCompletedOrders())
                    .acceptanceRate(shipper.getAcceptanceRate())
                    .gems(shipper.getGems())
                    .build();

        } catch (Exception e) {
            log.error("Lỗi lấy thông tin shipper từ token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED, "Token không hợp lệ");
        }
    }

    @Override
    public Long getShipperIdByPhone(String phone) {
        Shipper shipper = shipperRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy shipper"));
        return shipper.getId();
    }

    @Override
    @Cacheable(value = "activeDrivers", key = "#shipperId")
    public Shipper getShipperById(Long shipperId) {
        return shipperRepository.findById(shipperId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy shipper"));
    }

    // ===============================
    // LOCATION & TRACKING
    // ===============================

    @Override
    @CacheEvict(value = { "activeDrivers", "driverLocations" }, key = "#shipperId")
    public void updateLocation(Long shipperId, UpdateLocationRequest request) {
        Shipper shipper = getShipperById(shipperId);

        // Cập nhật vị trí
        shipper.updateLocation(request.getLatitude(), request.getLongitude());

        // Cập nhật trạng thái online nếu có
        if (request.getIsOnline() != null) {
            shipper.setIsOnline(request.getIsOnline());
        }

        shipperRepository.save(shipper);
        log.info("Đã cập nhật vị trí cho shipper ID: {}", shipperId);
    }

    @Override
    @Cacheable(value = "driverLocations", key = "#shipperId")
    public UpdateLocationRequest getCurrentLocation(Long shipperId) {
        Shipper shipper = getShipperById(shipperId);

        return UpdateLocationRequest.builder()
                .latitude(shipper.getCurrentLatitude())
                .longitude(shipper.getCurrentLongitude())
                .isOnline(shipper.getIsOnline())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ===============================
    // ORDER MANAGEMENT
    // ===============================

    @Override
    public List<DriverOrderResponse> getAvailableOrders(Long shipperId) {
        Shipper shipper = getShipperById(shipperId);

        // Chỉ lấy đơn hàng với status PROCESSING hoặc READY_FOR_PICKUP
        List<Order> availableOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PROCESSING ||
                        order.getStatus() == OrderStatus.READY_FOR_PICKUP)
                .filter(order -> !orderAssignmentRepository.existsByOrderIdAndShipperId(order.getId(), shipperId))
                .collect(Collectors.toList());

        return availableOrders.stream()
                .map(this::mapToDriverOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverOrderResponse> getAssignedOrders(Long shipperId) {
        List<OrderAssignment> assignments = orderAssignmentRepository.findByShipperIdAndStatus(
                shipperId, OrderAssignment.AssignmentStatus.ACCEPTED);

        return assignments.stream()
                .map(assignment -> mapToDriverOrderResponse(assignment.getOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverOrderResponse> getOrderHistory(Long shipperId, Pageable pageable) {
        List<OrderAssignment> assignments = orderAssignmentRepository.findByShipperId(shipperId);

        // Áp dụng pagination thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), assignments.size());

        List<OrderAssignment> pagedAssignments = assignments.subList(start, end);

        return pagedAssignments.stream()
                .map(assignment -> mapToDriverOrderResponse(assignment.getOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public DriverOrderResponse getOrderDetails(Long shipperId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra quyền truy cập
        if (!orderAssignmentRepository.existsByOrderIdAndShipperId(orderId, shipperId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền truy cập đơn hàng này");
        }

        return mapToDriverOrderResponse(order);
    }

    @Override
    public void acceptOrder(Long shipperId, Long orderId, OrderActionRequest request) {
        log.info("Shipper {} accept order {}", shipperId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Tìm assignment hiện tại
        OrderAssignment assignment = orderAssignmentRepository.findByOrderIdAndShipperId(orderId, shipperId)
                .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN, "Bạn không được assign đơn hàng này"));

        // Kiểm tra assignment đang ở trạng thái ASSIGNED
        if (assignment.getStatus() != OrderAssignment.AssignmentStatus.ASSIGNED) {
            throw new AppException(ErrorCode.INVALID_KEY, "Đơn hàng không ở trạng thái có thể accept");
        }

        // Accept assignment
        assignment.acceptOrder();

        // Set estimated times nếu có
        if (request != null) {
            if (request.getEstimatedPickupTime() != null) {
                assignment.setEstimatedPickupTime(
                        LocalDateTime.now().plusMinutes(request.getEstimatedPickupTime().longValue()));
            }
            if (request.getEstimatedDeliveryTime() != null) {
                assignment.setEstimatedDeliveryTime(
                        LocalDateTime.now().plusMinutes(request.getEstimatedDeliveryTime().longValue()));
            }
        }

        orderAssignmentRepository.save(assignment);

        // ✅ CANCEL TIMEOUT CHECK CHO SHIPPER NÀY
        orderAssignmentService.cancelTimeoutCheck(orderId, shipperId);

        // Cập nhật thống kê shipper
        Shipper shipper = getShipperById(shipperId);
        shipper.incrementTotalOrders();
        shipperRepository.save(shipper);

        log.info("Shipper {} đã accept order {} thành công", shipperId, orderId);
    }

    @Override
    public void rejectOrder(Long shipperId, Long orderId, OrderActionRequest request) {
        log.info("Shipper {} reject order {}", shipperId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Tìm assignment
        OrderAssignment assignment = orderAssignmentRepository.findByOrderIdAndShipperId(orderId, shipperId)
                .orElse(null);

        if (assignment == null) {
            // Tạo assignment mới nếu chưa có (edge case)
            assignment = OrderAssignment.builder()
                    .order(order)
                    .shipper(getShipperById(shipperId))
                    .status(OrderAssignment.AssignmentStatus.ASSIGNED)
                    .assignedAt(LocalDateTime.now())
                    .build();
        }

        // Từ chối đơn hàng
        String rejectionReason = request != null ? request.getRejectionReason() : "Không có lý do";
        assignment.rejectOrder(rejectionReason);
        orderAssignmentRepository.save(assignment);

        log.info("Đã lưu rejection: Shipper {} reject order {} - lý do: {}", shipperId, orderId, rejectionReason);

        // ✅ CANCEL TIMEOUT CHECK CHO SHIPPER NÀY
        orderAssignmentService.cancelTimeoutCheck(orderId, shipperId);

        // ✅ TỰ ĐỘNG TÌM SHIPPER KHÁC (RETRY LOGIC)
        boolean foundNewShipper = orderAssignmentService.handleOrderRejection(orderId, shipperId);

        if (foundNewShipper) {
            log.info("Đã tìm được shipper khác cho order {} sau khi shipper {} reject", orderId, shipperId);
        } else {
            log.warn("Không tìm được shipper khác cho order {} sau khi shipper {} reject", orderId, shipperId);
        }
    }

    @Override
    public void updateOrderStatus(Long shipperId, Long orderId, String status, OrderActionRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (!orderAssignmentRepository.existsByOrderIdAndShipperId(orderId, shipperId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền cập nhật đơn hàng này");
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            orderRepository.save(order);

            log.info("Đã cập nhật trạng thái đơn hàng {} thành {}", orderId, status);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_KEY, "Trạng thái đơn hàng không hợp lệ");
        }
    }

    @Override
    public void confirmPickup(Long shipperId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (!orderAssignmentRepository.existsByOrderIdAndShipperId(orderId, shipperId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền xác nhận đơn hàng này");
        }

        // Cập nhật trạng thái
        order.setStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);

        log.info("Shipper {} đã xác nhận lấy hàng cho đơn {}", shipperId, orderId);
    }

    @Override
    public void confirmDelivery(Long shipperId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (!orderAssignmentRepository.existsByOrderIdAndShipperId(orderId, shipperId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền xác nhận đơn hàng này");
        }

        // Cập nhật trạng thái
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // Cập nhật thống kê shipper
        Shipper shipper = getShipperById(shipperId);
        shipper.incrementCompletedOrders();
        shipperRepository.save(shipper);

        // Có thể thêm logic tính toán earnings và gems ở đây

        log.info("Shipper {} đã xác nhận giao hàng thành công cho đơn {}", shipperId, orderId);
    }

    @Override
    public Integer getPendingOrdersCount(Long shipperId) {
        List<OrderAssignment> pendingAssignments = orderAssignmentRepository
                .findPendingAssignmentsByShipperId(shipperId);
        return pendingAssignments.size();
    }

    // ===============================
    // PROFILE MANAGEMENT (Phase 2)
    // ===============================

    @Override
    public DriverLoginResponse getProfile(Long shipperId) {
        Shipper shipper = getShipperById(shipperId);

        return DriverLoginResponse.builder()
                .shipperId(shipper.getId())
                .name(shipper.getName())
                .phone(shipper.getPhone())
                .email(shipper.getEmail())
                .rating(shipper.getRating())
                .status(shipper.getStatus().name())
                .isOnline(shipper.getIsOnline())
                .vehicleType(shipper.getVehicleType())
                .licensePlate(shipper.getLicensePlate())
                .totalOrders(shipper.getTotalOrders())
                .completedOrders(shipper.getCompletedOrders())
                .acceptanceRate(shipper.getAcceptanceRate())
                .gems(shipper.getGems())
                .currentLatitude(shipper.getCurrentLatitude())
                .currentLongitude(shipper.getCurrentLongitude())
                .build();
    }

    @Override
    public void updateProfile(Long shipperId, UpdateProfileRequest request) {
        Shipper shipper = getShipperById(shipperId);

        // Cập nhật thông tin cơ bản
        if (request.getName() != null) {
            shipper.setName(request.getName());
        }
        if (request.getEmail() != null) {
            shipper.setEmail(request.getEmail());
        }
        if (request.getVehicleType() != null) {
            shipper.setVehicleType(request.getVehicleType());
        }
        if (request.getLicensePlate() != null) {
            shipper.setLicensePlate(request.getLicensePlate());
        }

        shipperRepository.save(shipper);
        log.info("Đã cập nhật profile cho shipper ID: {}", shipperId);
    }

    @Override
    public ProfileStatsResponse getProfileStats(Long shipperId) {
        Shipper shipper = getShipperById(shipperId);

        // Lấy thống kê từ database
        List<OrderAssignment> allAssignments = orderAssignmentRepository.findByShipperId(shipperId);

        int totalOrders = shipper.getTotalOrders();
        int completedOrders = shipper.getCompletedOrders();
        int cancelledOrders = (int) allAssignments.stream()
                .filter(a -> a.getStatus() == OrderAssignment.AssignmentStatus.REJECTED)
                .count();

        double acceptanceRate = shipper.getAcceptanceRate();
        double completionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;

        // Lấy thống kê thu nhập
        Long todayEarnings = transactionRepository.getTodayEarningsByShipper(shipperId);
        Long weekEarnings = transactionRepository.getWeekEarningsByShipper(shipperId);
        Long monthEarnings = transactionRepository.getMonthEarningsByShipper(shipperId);

        return ProfileStatsResponse.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .acceptanceRate(acceptanceRate)
                .completionRate(completionRate)
                .rating(shipper.getRating().doubleValue()) // Convert BigDecimal to Double
                .totalReviews(0) // Có thể thêm sau
                .onlineHoursToday(0) // Cần implement tracking time
                .onlineHoursThisWeek(0)
                .onlineHoursThisMonth(0)
                .totalEarnings(BigDecimal.valueOf(monthEarnings != null ? monthEarnings : 0))
                .todayEarnings(BigDecimal.valueOf(todayEarnings != null ? todayEarnings : 0))
                .weekEarnings(BigDecimal.valueOf(weekEarnings != null ? weekEarnings : 0))
                .monthEarnings(BigDecimal.valueOf(monthEarnings != null ? monthEarnings : 0))
                .averageEarningPerOrder(
                        totalOrders > 0 ? BigDecimal.valueOf((monthEarnings != null ? monthEarnings : 0) / totalOrders)
                                : BigDecimal.ZERO)
                .totalGems(shipper.getGems())
                .gemsEarnedThisMonth(0) // Có thể thêm sau
                .totalRewardsClaimed(0) // Từ ShipperRewardRepository
                .availableRewards(0)
                .totalDistanceKm(0.0) // Cần implement tracking
                .averageDeliveryTimeMinutes(30) // Ước tính
                .currentRank("BRONZE") // Có thể tính toán dựa trên rating/orders
                .rankPosition(0)
                .achievements(new String[] {}) // Có thể thêm sau
                .lastUpdated(LocalDateTime.now())
                .memberSince(shipper.getCreatedDate()) // Use createdDate instead of createdAt
                .build();
    }

    @Override
    public void uploadAvatar(Long shipperId, String imageUrl) {
        Shipper shipper = getShipperById(shipperId);
        // Note: profile_image_url field doesn't exist in Shipper entity
        // This method will need database schema update to add the field
        log.info(
                "Avatar upload not implemented - profile_image_url field missing in database schema for shipper ID: {}",
                shipperId);
        // shipper.setProfileImageUrl(imageUrl);
        // shipperRepository.save(shipper);
    }

    // ===============================
    // WALLET & FINANCIAL (Phase 2)
    // ===============================

    @Override
    public WalletResponse getWallet(Long shipperId) {
        Shipper shipper = getShipperById(shipperId);

        // Tìm ví của shipper
        Wallet wallet = walletRepository.findByShipperId(shipperId)
                .orElse(null);

        if (wallet == null) {
            // Tạo ví mới nếu chưa có
            wallet = Wallet.builder()
                    .shipper(shipper)
                    .currentBalance(0L)
                    .todayEarnings(0L)
                    .weekEarnings(0L)
                    .monthEarnings(0L)
                    .totalEarnings(0L)
                    .codHolding(0L)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            wallet = walletRepository.save(wallet);
        }

        // Đếm giao dịch
        List<Transaction> allTransactions = transactionRepository.findByShipperIdOrderByTransactionDateDesc(shipperId);
        int totalTransactions = allTransactions.size();
        int pendingTransactions = (int) allTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING)
                .count();
        int completedTransactions = (int) allTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .count();

        return WalletResponse.builder()
                .walletId(wallet.getId())
                .shipperId(shipperId)
                .shipperName(shipper.getName())
                .currentBalance(wallet.getCurrentBalance())
                .todayEarnings(wallet.getTodayEarnings())
                .weekEarnings(wallet.getWeekEarnings())
                .monthEarnings(wallet.getMonthEarnings())
                .totalEarnings(wallet.getTotalEarnings())
                .codHolding(wallet.getCodHolding())
                .pendingAmount(0L) // Có thể tính từ pending transactions
                .withdrawableAmount(wallet.getCurrentBalance()) // Số tiền có thể rút
                .totalTransactions(totalTransactions)
                .pendingTransactions(pendingTransactions)
                .completedTransactions(completedTransactions)
                .lastUpdated(wallet.getLastUpdated())
                .lastEarning(null) // Có thể lấy từ transaction gần nhất
                .lastWithdrawal(null)
                .bankName(null) // Chưa có trong entity
                .bankAccountNumber(null)
                .bankAccountHolder(null)
                .isVerified(false)
                .dailyWithdrawLimit(5000000L) // 5 triệu VND
                .monthlyWithdrawLimit(50000000L) // 50 triệu VND
                .autoWithdraw(false)
                .autoWithdrawThreshold(1000000L) // 1 triệu VND
                .status("ACTIVE")
                .currency("VND")
                .build();
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(Long shipperId, Pageable pageable) {
        List<Transaction> transactions = transactionRepository.findByShipperIdOrderByTransactionDateDesc(shipperId);

        // Áp dụng pagination thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), transactions.size());

        List<Transaction> pagedTransactions = transactions.subList(start, end);

        return pagedTransactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByType(Long shipperId, String type) {
        try {
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionRepository
                    .findByShipperIdAndTypeOrderByTransactionDateDesc(shipperId, transactionType);

            return transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_KEY, "Loại giao dịch không hợp lệ");
        }
    }

    @Override
    public void withdrawMoney(Long shipperId, WithdrawRequest request) {
        Shipper shipper = getShipperById(shipperId);

        // Kiểm tra ví
        Wallet wallet = walletRepository.findByShipperId(shipperId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND, "Không tìm thấy ví"));

        // Kiểm tra số dư
        if (wallet.getCurrentBalance() < request.getAmount()) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE, "Số dư không đủ để rút tiền");
        }

        // Kiểm tra giới hạn rút tiền
        if (request.getAmount() > 5000000L) { // 5 triệu VND
            throw new AppException(ErrorCode.WITHDRAW_LIMIT_EXCEEDED, "Vượt quá giới hạn rút tiền hàng ngày");
        }

        // Tạo giao dịch rút tiền
        Transaction withdrawal = Transaction.builder()
                .shipper(shipper)
                .type(Transaction.TransactionType.TOP_UP)
                .status(Transaction.TransactionStatus.PENDING)
                .amount(-request.getAmount())
                .description("Rút tiền từ ví - " + request.getBankName() + " - " + request.getBankAccountNumber())
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(withdrawal);

        // Trừ tiền từ ví (có thể đặt về PENDING và chờ admin approve)
        wallet.deductBalance(request.getAmount());
        walletRepository.save(wallet);

        log.info("Shipper {} đã tạo yêu cầu rút {} VND", shipperId, request.getAmount());
    }

    @Override
    public WalletResponse getEarningsStats(Long shipperId) {
        return getWallet(shipperId); // Tương tự như getWallet
    }

    @Override
    public Boolean canWithdraw(Long shipperId, Long amount) {
        Wallet wallet = walletRepository.findByShipperId(shipperId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND, "Không tìm thấy ví của shipper"));

        // Kiểm tra số dư khả dụng (trừ tiền COD đang giữ)
        Long availableBalance = wallet.getCurrentBalance() - wallet.getCodHolding();
        return availableBalance >= amount;
    }

    // ===============================
    // REWARDS SYSTEM (Phase 3)
    // ===============================

    @Override
    public List<RewardResponse> getAvailableRewards(Long shipperId) {
        log.info("Lấy danh sách phần thưởng khả dụng cho shipper: {}", shipperId);

        // Lấy tất cả rewards đang active
        List<Reward> activeRewards = rewardRepository.findValidRewards(LocalDate.now());

        return activeRewards.stream()
                .map(reward -> mapToRewardResponse(reward, shipperId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardResponse> getClaimedRewards(Long shipperId, Pageable pageable) {
        log.info("Lấy lịch sử phần thưởng đã nhận của shipper: {}", shipperId);

        List<ShipperReward> claimedRewards = shipperRewardRepository.findClaimedRewardsByShipper(shipperId);

        return claimedRewards.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(this::mapShipperRewardToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void claimReward(Long shipperId, Long rewardId) {
        log.info("Shipper {} đang claim reward {}", shipperId, rewardId);

        Shipper shipper = getShipperById(shipperId);
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy phần thưởng"));

        // Kiểm tra reward có thể claim không
        if (!reward.getIsActive() || !reward.getStatus().equals(Reward.RewardStatus.ACTIVE)) {
            throw new AppException(ErrorCode.INVALID_KEY, "Phần thưởng không còn hiệu lực");
        }

        // Kiểm tra shipper đã claim reward này chưa
        if (shipperRewardRepository.existsByShipperIdAndRewardId(shipperId, rewardId)) {
            throw new AppException(ErrorCode.INVALID_KEY, "Bạn đã nhận phần thưởng này rồi");
        }

        // Kiểm tra điều kiện reward
        if (!checkRewardEligibility(shipper, reward)) {
            throw new AppException(ErrorCode.INVALID_KEY, "Bạn chưa đủ điều kiện nhận phần thưởng này");
        }

        // Tạo ShipperReward
        ShipperReward shipperReward = ShipperReward.builder()
                .shipper(shipper)
                .reward(reward)
                .status(ShipperReward.RewardClaimStatus.CLAIMED)
                .claimedAt(LocalDateTime.now())
                .completionPercentage(100.0f)
                .build();

        shipperRewardRepository.save(shipperReward);

        // Cập nhật wallet và gems cho shipper
        updateShipperRewardWallet(shipper, reward);

        log.info("Shipper {} đã nhận thành công reward {} - {}", shipperId, rewardId, reward.getName());
    }

    @Override
    public List<RewardResponse> getRewardProgress(Long shipperId) {
        log.info("Lấy tiến độ phần thưởng của shipper: {}", shipperId);

        List<ShipperReward> shipperRewards = shipperRewardRepository.findByShipperIdOrderByClaimedAtDesc(shipperId);

        return shipperRewards.stream()
                .map(this::mapShipperRewardToResponse)
                .collect(Collectors.toList());
    }

    // ===============================
    // ANALYTICS (Phase 3)
    // ===============================

    @Override
    public AnalyticsResponse getPerformanceAnalytics(Long shipperId, String period) {
        log.info("Lấy thống kê hiệu suất shipper {} trong kỳ: {}", shipperId, period);

        Shipper shipper = getShipperById(shipperId);
        LocalDateTime[] dateRange = getDateRangeByPeriod(period);

        // Lấy dữ liệu đơn hàng trong kỳ
        List<Order> orders = getOrdersInPeriod(shipperId, dateRange[0], dateRange[1]);
        List<Transaction> transactions = getTransactionsInPeriod(shipperId, dateRange[0], dateRange[1]);

        return AnalyticsResponse.builder()
                .shipperId(shipperId)
                .shipperName(shipper.getName())
                .period(period)
                .totalOrders(orders.size())
                .completedOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count())
                .cancelledOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
                .rejectedOrders(getrejectedOrdersCount(shipperId, dateRange[0], dateRange[1]))
                .completionRate(calculateCompletionRate(orders))
                .acceptanceRate(shipper.getAcceptanceRate().doubleValue())
                .totalWorkingHours(calculateWorkingHours(shipperId, dateRange[0], dateRange[1]))
                .onlineHours(calculateOnlineHours(shipperId, dateRange[0], dateRange[1]))
                .averageDeliveryTime(calculateAverageDeliveryTime(orders))
                .totalDistance(calculateTotalDistance(orders))
                .totalEarnings(calculateTotalEarnings(transactions))
                .currentRating(shipper.getRating().doubleValue())
                .totalReviews(0) // Có thể implement sau
                .totalGems(shipper.getGems())
                .reportGeneratedAt(LocalDateTime.now())
                .periodStart(dateRange[0])
                .periodEnd(dateRange[1])
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Override
    public AnalyticsResponse getEarningsAnalytics(Long shipperId, String period) {
        log.info("Lấy phân tích thu nhập shipper {} trong kỳ: {}", shipperId, period);

        Shipper shipper = getShipperById(shipperId);
        LocalDateTime[] dateRange = getDateRangeByPeriod(period);

        List<Transaction> transactions = getTransactionsInPeriod(shipperId, dateRange[0], dateRange[1]);
        List<Order> orders = getOrdersInPeriod(shipperId, dateRange[0], dateRange[1]);

        // Tính toán thu nhập từ Orders thay vì Transactions để có dữ liệu chính xác hơn
        BigDecimal totalEarnings = calculateShipperEarningsFromOrders(orders);
        BigDecimal deliveryFees = calculateDeliveryFeesFromOrders(orders);
        BigDecimal tips = calculateTipsFromOrders(orders);
        BigDecimal bonuses = calculateBonuses(transactions); // Bonus vẫn lấy từ transactions

        return AnalyticsResponse.builder()
                .shipperId(shipperId)
                .shipperName(shipper.getName())
                .period(period)
                .totalEarnings(totalEarnings)
                .deliveryFees(deliveryFees)
                .tips(tips)
                .bonuses(bonuses)
                .averageEarningPerOrder(orders.isEmpty() ? BigDecimal.ZERO
                        : totalEarnings.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP))
                .averageEarningPerHour(calculateEarningPerHour(totalEarnings, shipperId, dateRange[0], dateRange[1]))
                .dailyEarnings(calculateDailyEarnings(transactions))
                .reportGeneratedAt(LocalDateTime.now())
                .periodStart(dateRange[0])
                .periodEnd(dateRange[1])
                .build();
    }

    @Override
    public AnalyticsResponse getOrderAnalytics(Long shipperId, String period) {
        log.info("Lấy thống kê đơn hàng shipper {} trong kỳ: {}", shipperId, period);

        Shipper shipper = getShipperById(shipperId);
        LocalDateTime[] dateRange = getDateRangeByPeriod(period);

        List<Order> orders = getOrdersInPeriod(shipperId, dateRange[0], dateRange[1]);

        return AnalyticsResponse.builder()
                .shipperId(shipperId)
                .shipperName(shipper.getName())
                .period(period)
                .totalOrders(orders.size())
                .completedOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count())
                .cancelledOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
                .hourlyOrders(calculateHourlyOrders(orders))
                .dailyOrders(calculateDailyOrders(orders))
                .peakHours(calculatePeakHours(orders))
                .averageDeliveryTime(calculateAverageDeliveryTime(orders))
                .fastestDelivery(calculateFastestDelivery(orders))
                .slowestDelivery(calculateSlowestDelivery(orders))
                .totalDistance(calculateTotalDistance(orders))
                .averageDistance(calculateAverageDistance(orders))
                .reportGeneratedAt(LocalDateTime.now())
                .periodStart(dateRange[0])
                .periodEnd(dateRange[1])
                .build();
    }

    // ===============================
    // SYSTEM UTILITIES (Phase 3)
    // ===============================

    @Override
    public SystemResponse checkAppVersion(String currentVersion) {
        log.info("Kiểm tra phiên bản app: {}", currentVersion);

        // Hardcode latest version - có thể lưu trong database
        String latestVersion = "2.1.0";
        Integer currentBuild = parseVersionToBuild(currentVersion);
        Integer latestBuild = parseVersionToBuild(latestVersion);

        boolean needsUpdate = currentBuild < latestBuild;
        boolean forceUpdate = (latestBuild - currentBuild) > 5; // Force update nếu quá cũ

        return SystemResponse.builder()
                .currentVersion(currentVersion)
                .latestVersion(latestVersion)
                .needsUpdate(needsUpdate)
                .forceUpdate(forceUpdate)
                .updateUrl("https://play.google.com/store/apps/details?id=com.grabfood.driver")
                .changelog("- Cải thiện hiệu suất\n- Sửa lỗi nhỏ\n- Thêm tính năng mới")
                .buildNumber(latestBuild)
                .systemStatus("ONLINE")
                .build();
    }

    @Override
    public SystemResponse submitFeedback(Long shipperId, FeedbackRequest request) {
        log.info("Shipper {} gửi feedback: {}", shipperId, request.getTitle());

        Shipper shipper = getShipperById(shipperId);

        // Tạo ticket number
        String ticketNumber = "FEEDBACK-" + System.currentTimeMillis() + "-" + shipperId;

        // Trong thực tế sẽ lưu vào database feedback table
        log.info("Feedback được gửi: Type={}, Title={}, Shipper={}",
                request.getType(), request.getTitle(), shipper.getName());

        return SystemResponse.builder()
                .feedbackId(System.currentTimeMillis()) // Mock ID
                .feedbackStatus("SUBMITTED")
                .responseMessage("Cảm ơn bạn đã gửi phản hồi. Chúng tôi sẽ xem xét và phản hồi trong 24-48 giờ.")
                .submittedAt(LocalDateTime.now())
                .ticketNumber(ticketNumber)
                .build();
    }

    @Override
    public SystemResponse getSupportInfo() {
        log.info("Lấy thông tin hỗ trợ hệ thống");

        List<SystemResponse.ContactInfo> contacts = Arrays.asList(
                SystemResponse.ContactInfo.builder()
                        .type("PHONE")
                        .label("Hotline 24/7")
                        .value("1900-1234")
                        .description("Hỗ trợ khẩn cấp 24/7")
                        .isAvailable(true)
                        .availableHours("24/7")
                        .build(),
                SystemResponse.ContactInfo.builder()
                        .type("EMAIL")
                        .label("Email hỗ trợ")
                        .value("support@grabfood.vn")
                        .description("Gửi email cho đội ngũ hỗ trợ")
                        .isAvailable(true)
                        .availableHours("24/7")
                        .build());

        List<SystemResponse.FAQItem> faqs = Arrays.asList(
                SystemResponse.FAQItem.builder()
                        .question("Làm sao để cập nhật vị trí?")
                        .answer("Vào Settings > Location và bật GPS")
                        .category("LOCATION")
                        .priority(1)
                        .build(),
                SystemResponse.FAQItem.builder()
                        .question("Tại sao tôi không nhận được đơn hàng?")
                        .answer("Kiểm tra kết nối mạng và đảm bảo bạn đang online")
                        .category("ORDERS")
                        .priority(2)
                        .build());

        return SystemResponse.builder()
                .supportHotline("1900-1234")
                .supportEmail("support@grabfood.vn")
                .faqUrl("https://help.grabfood.vn")
                .userGuideUrl("https://help.grabfood.vn/driver-guide")
                .supportContacts(contacts)
                .frequentQuestions(faqs)
                .systemStatus("ONLINE")
                .build();
    }

    // ===============================
    // HELPER METHODS FOR PHASE 3
    // ===============================

    private RewardResponse mapToRewardResponse(Reward reward, Long shipperId) {
        // Kiểm tra shipper đã có reward này chưa
        Optional<ShipperReward> shipperReward = shipperRewardRepository.findByShipperIdAndRewardId(shipperId,
                reward.getId());

        String shipperStatus = "ELIGIBLE";
        Float progressValue = 0.0f;
        Float completionPercentage = 0.0f;
        LocalDateTime claimedAt = null;

        if (shipperReward.isPresent()) {
            ShipperReward sr = shipperReward.get();
            shipperStatus = sr.getStatus().name();
            progressValue = sr.getProgressValue();
            completionPercentage = sr.getCompletionPercentage();
            claimedAt = sr.getClaimedAt();
        } else {
            // Tính toán progress cho reward mới
            Shipper shipper = getShipperById(shipperId);
            completionPercentage = calculateRewardProgress(shipper, reward);
        }

        return RewardResponse.builder()
                .rewardId(reward.getId())
                .title(reward.getTitle())
                .name(reward.getName())
                .description(reward.getDescription())
                .iconUrl(reward.getIconUrl())
                .type(reward.getType().name())
                .rewardValue(reward.getRewardValue())
                .gemsValue(reward.getGemsValue())
                .status(reward.getStatus().name())
                .requiredOrders(reward.getRequiredOrders())
                .requiredDeliveries(reward.getRequiredDeliveries())
                .requiredDistance(reward.getRequiredDistance())
                .requiredRating(reward.getRequiredRating())
                .startDate(reward.getStartDate())
                .endDate(reward.getEndDate())
                .peakStartTime(reward.getPeakStartTime() != null ? reward.getPeakStartTime().toString() : null)
                .peakEndTime(reward.getPeakEndTime() != null ? reward.getPeakEndTime().toString() : null)
                .shipperStatus(shipperStatus)
                .progressValue(progressValue)
                .completionPercentage(completionPercentage)
                .claimedAt(claimedAt)
                .isActive(reward.getIsActive())
                .canClaim(completionPercentage >= 100.0f && !shipperReward.isPresent())
                .build();
    }

    private RewardResponse mapShipperRewardToResponse(ShipperReward shipperReward) {
        Reward reward = shipperReward.getReward();

        return RewardResponse.builder()
                .rewardId(reward.getId())
                .title(reward.getTitle())
                .name(reward.getName())
                .description(reward.getDescription())
                .iconUrl(reward.getIconUrl())
                .type(reward.getType().name())
                .rewardValue(reward.getRewardValue())
                .gemsValue(reward.getGemsValue())
                .status(reward.getStatus().name())
                .shipperStatus(shipperReward.getStatus().name())
                .progressValue(shipperReward.getProgressValue())
                .completionPercentage(shipperReward.getCompletionPercentage())
                .claimedAt(shipperReward.getClaimedAt())
                .progressNotes(shipperReward.getNotes())
                .isActive(reward.getIsActive())
                .canClaim(false) // Đã claim rồi
                .build();
    }

    private boolean checkRewardEligibility(Shipper shipper, Reward reward) {
        // Kiểm tra điều kiện số đơn hàng
        if (reward.getRequiredOrders() != null && shipper.getTotalOrders() < reward.getRequiredOrders()) {
            return false;
        }

        // Kiểm tra điều kiện rating
        if (reward.getRequiredRating() != null && shipper.getRating().floatValue() < reward.getRequiredRating()) {
            return false;
        }

        // Có thể thêm các điều kiện khác
        return true;
    }

    private Float calculateRewardProgress(Shipper shipper, Reward reward) {
        if (reward.getRequiredOrders() != null) {
            return Math.min(100.0f, (shipper.getTotalOrders() * 100.0f) / reward.getRequiredOrders());
        }

        if (reward.getRequiredRating() != null) {
            return Math.min(100.0f, (shipper.getRating().floatValue() * 100.0f) / reward.getRequiredRating());
        }

        return 0.0f;
    }

    private void updateShipperRewardWallet(Shipper shipper, Reward reward) {
        // Cập nhật gems
        if (reward.getGemsValue() != null && reward.getGemsValue() > 0) {
            shipper.setGems(shipper.getGems() + reward.getGemsValue());
            shipperRepository.save(shipper);
        }

        // Cập nhật wallet nếu có reward value
        if (reward.getRewardValue() != null && reward.getRewardValue().compareTo(BigDecimal.ZERO) > 0) {
            Wallet wallet = walletRepository.findByShipperId(shipper.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND, "Không tìm thấy ví"));

            wallet.setCurrentBalance(wallet.getCurrentBalance() + reward.getRewardValue().longValue());
            wallet.setLastUpdated(LocalDateTime.now());
            walletRepository.save(wallet);

            // Tạo transaction
            Transaction rewardTransaction = Transaction.builder()
                    .shipper(shipper)
                    .type(Transaction.TransactionType.BONUS)
                    .status(Transaction.TransactionStatus.COMPLETED)
                    .amount(reward.getRewardValue().longValue())
                    .description("Nhận thưởng: " + reward.getName())
                    .transactionDate(LocalDateTime.now())
                    .build();

            transactionRepository.save(rewardTransaction);
        }
    }

    private LocalDateTime[] getDateRangeByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end = now;

        switch (period.toLowerCase()) {
            case "today":
                start = now.toLocalDate().atStartOfDay();
                break;
            case "week":
                start = now.minusWeeks(1);
                break;
            case "month":
                start = now.minusMonths(1);
                break;
            case "year":
                start = now.minusYears(1);
                break;
            default:
                start = now.minusDays(1); // Default to yesterday
        }

        return new LocalDateTime[] { start, end };
    }

    private List<Order> getOrdersInPeriod(Long shipperId, LocalDateTime start, LocalDateTime end) {
        // Lấy orders trực tiếp theo shipper_id trong khoảng thời gian
        return orderRepository.findAll().stream()
                .filter(order -> order.getShipperId() != null && order.getShipperId().equals(shipperId))
                .filter(order -> order.getOrderDate().isAfter(start) && order.getOrderDate().isBefore(end))
                .collect(Collectors.toList());
    }

    private List<Transaction> getTransactionsInPeriod(Long shipperId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getShipper().getId().equals(shipperId))
                .filter(transaction -> transaction.getTransactionDate().isAfter(start)
                        && transaction.getTransactionDate().isBefore(end))
                .collect(Collectors.toList());
    }

    private Integer getrejectedOrdersCount(Long shipperId, LocalDateTime start, LocalDateTime end) {
        // Đếm số đơn bị reject trong khoảng thời gian
        return (int) orderAssignmentRepository.findAll().stream()
                .filter(assignment -> assignment.getShipper().getId().equals(shipperId))
                .filter(assignment -> assignment.getAssignedAt().isAfter(start)
                        && assignment.getAssignedAt().isBefore(end))
                .filter(assignment -> assignment.getStatus() == OrderAssignment.AssignmentStatus.REJECTED)
                .count();
    }

    private Double calculateCompletionRate(List<Order> orders) {
        if (orders.isEmpty())
            return 0.0;

        long completedCount = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .count();

        return (completedCount * 100.0) / orders.size();
    }

    private Integer calculateWorkingHours(Long shipperId, LocalDateTime start, LocalDateTime end) {
        // Tính toán giờ làm việc dựa trên khoảng thời gian có đơn hàng
        List<OrderAssignment> assignments = orderAssignmentRepository.findAssignmentsByShipperAndTimeBetween(
                shipperId, start, end);

        if (assignments.isEmpty())
            return 0;

        // Tính từ assignment đầu tiên đến cuối cùng trong ngày
        LocalDateTime firstOrder = assignments.stream()
                .map(OrderAssignment::getAssignedAt)
                .min(LocalDateTime::compareTo)
                .orElse(start);

        LocalDateTime lastOrder = assignments.stream()
                .map(assignment -> assignment.getOrder().getDeliveredAt())
                .filter(deliveredAt -> deliveredAt != null)
                .max(LocalDateTime::compareTo)
                .orElse(end);

        return (int) java.time.Duration.between(firstOrder, lastOrder).toHours();
    }

    private Integer calculateOnlineHours(Long shipperId, LocalDateTime start, LocalDateTime end) {
        // Ước tính thời gian online = thời gian làm việc + 20%
        Integer workingHours = calculateWorkingHours(shipperId, start, end);
        return (int) (workingHours * 1.2);
    }

    private Integer calculateAverageDeliveryTime(List<Order> orders) {
        // Tính thời gian giao hàng trung bình từ dữ liệu thực
        List<Integer> deliveryTimes = orders.stream()
                .map(Order::getDeliveryTimeInMinutes)
                .filter(time -> time != null && time > 0)
                .collect(Collectors.toList());

        if (deliveryTimes.isEmpty()) {
            return 0;
        }

        return (int) deliveryTimes.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private Double calculateTotalDistance(List<Order> orders) {
        // Tính tổng quãng đường từ dữ liệu thực
        return orders.stream()
                .filter(order -> order.getDistanceKm() != null)
                .mapToDouble(order -> order.getDistanceKm().doubleValue())
                .sum();
    }

    private BigDecimal calculateTotalEarnings(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == Transaction.TransactionType.EARNING)
                .map(transaction -> BigDecimal.valueOf(transaction.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDeliveryFees(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == Transaction.TransactionType.EARNING)
                .map(transaction -> BigDecimal
                        .valueOf(transaction.getDeliveryFee() != null ? transaction.getDeliveryFee() : 0))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTips(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == Transaction.TransactionType.TIP)
                .map(transaction -> BigDecimal.valueOf(transaction.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateBonuses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == Transaction.TransactionType.BONUS)
                .map(transaction -> BigDecimal.valueOf(transaction.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateEarningPerHour(BigDecimal totalEarnings, Long shipperId, LocalDateTime start,
            LocalDateTime end) {
        Integer workingHours = calculateWorkingHours(shipperId, start, end);
        if (workingHours == 0)
            return BigDecimal.ZERO;
        return totalEarnings.divide(BigDecimal.valueOf(workingHours), 2, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> calculateDailyEarnings(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == Transaction.TransactionType.EARNING)
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().toLocalDate().toString(),
                        Collectors.mapping(
                                transaction -> BigDecimal.valueOf(transaction.getAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private Map<String, Integer> calculateHourlyOrders(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> String.valueOf(order.getOrderDate().getHour()),
                        Collectors.summingInt(order -> 1)));
    }

    private Map<String, Integer> calculateDailyOrders(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().getDayOfWeek().toString(),
                        Collectors.summingInt(order -> 1)));
    }

    private List<AnalyticsResponse.PeakHourData> calculatePeakHours(List<Order> orders) {
        Map<String, List<Order>> hourlyGroups = orders.stream()
                .collect(Collectors.groupingBy(order -> {
                    int hour = order.getOrderDate().getHour();
                    if (hour >= 11 && hour <= 13)
                        return "11:00-13:00";
                    if (hour >= 17 && hour <= 19)
                        return "17:00-19:00";
                    return "Other";
                }));

        return hourlyGroups.entrySet().stream()
                .filter(entry -> !"Other".equals(entry.getKey()))
                .map(entry -> AnalyticsResponse.PeakHourData.builder()
                        .timeSlot(entry.getKey())
                        .orderCount(entry.getValue().size())
                        .earnings(BigDecimal.valueOf(entry.getValue().size() * 25000)) // Mock calculation
                        .averageDeliveryTime(calculateAverageDeliveryTime(entry.getValue()).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    private Integer calculateFastestDelivery(List<Order> orders) {
        // Tìm thời gian giao hàng nhanh nhất từ dữ liệu thực
        return orders.stream()
                .map(Order::getDeliveryTimeInMinutes)
                .filter(time -> time != null && time > 0)
                .min(Integer::compareTo)
                .orElse(0);
    }

    private Integer calculateSlowestDelivery(List<Order> orders) {
        // Tìm thời gian giao hàng chậm nhất từ dữ liệu thực
        return orders.stream()
                .map(Order::getDeliveryTimeInMinutes)
                .filter(time -> time != null && time > 0)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private Double calculateAverageDistance(List<Order> orders) {
        // Tính khoảng cách trung bình từ dữ liệu thực
        List<BigDecimal> distances = orders.stream()
                .filter(order -> order.getDistanceKm() != null)
                .map(Order::getDistanceKm)
                .collect(Collectors.toList());

        if (distances.isEmpty()) {
            return 0.0;
        }

        return distances.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Integer parseVersionToBuild(String version) {
        // Parse version string "x.y.z" to build number
        try {
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]) * 1000 +
                    Integer.parseInt(parts[1]) * 100 +
                    Integer.parseInt(parts[2]);
        } catch (Exception e) {
            return 0;
        }
    }

    // ===============================
    // UTILITY METHODS
    // ===============================

    /**
     * Chuyển đổi Order entity thành DriverOrderResponse
     */
    private DriverOrderResponse mapToDriverOrderResponse(Order order) {
        // Lấy thông tin restaurant từ cart details
        Restaurant restaurant = getRestaurantByOrder(order);

        // Lấy thông tin user
        User customer = order.getUser();

        // Lấy cart details và convert thành order items
        List<DriverOrderResponse.OrderItemResponse> items = order.getCartDetails().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return DriverOrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .customerName(customer.getName())
                .customerPhone(customer.getPhone())
                .pickupAddress(restaurant.getAddress().getDetail())
                .deliveryAddress(order.getAddress())
                .pickupLatitude(restaurant.getAddress().getLat())
                .pickupLongitude(restaurant.getAddress().getLon())
                .deliveryLatitude(order.getLatitude()) // Sử dụng dữ liệu thực từ database
                .deliveryLongitude(order.getLongitude()) // Sử dụng dữ liệu thực từ database
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .deliveryDistance(order.getDistanceKm() != null ? order.getDistanceKm() : BigDecimal.ZERO) // Dữ liệu
                                                                                                           // thực
                .estimatedTime(order.getDeliveryTimeInMinutes() != null ? order.getDeliveryTimeInMinutes() : 30) // Dữ
                                                                                                                 // liệu
                                                                                                                 // thực
                                                                                                                 // hoặc
                                                                                                                 // default
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD") // Dữ liệu thực
                .assignedAt(LocalDateTime.now()) // Có thể lấy từ OrderAssignment
                .acceptedAt(null)
                .pickedUpAt(order.getPickedUpAt()) // Dữ liệu thực từ database
                .deliveredAt(order.getDeliveredAt()) // Dữ liệu thực từ database
                .tip(order.getTipAmount() != null ? order.getTipAmount().longValue() : 0L) // Dữ liệu thực
                .gemsEarned(0)
                .shipperEarning(order.getShipperEarning() != null ? order.getShipperEarning() : BigDecimal.ZERO) // Dữ
                                                                                                                 // liệu
                                                                                                                 // thực
                .restaurantName(restaurant.getName())
                .restaurantPhone(restaurant.getPhone())
                .restaurantAddress(restaurant.getAddress().getDetail())
                .items(items)
                .build();
    }

    /**
     * Chuyển đổi CartDetail thành OrderItemResponse
     */
    private DriverOrderResponse.OrderItemResponse mapToOrderItemResponse(CartDetail cartDetail) {
        // Lấy FoodDetail để có thông tin price
        FoodDetail foodDetail = cartDetail.getFood().getFoodDetails().stream()
                .findFirst()
                .orElse(null);

        BigDecimal price = foodDetail != null ? foodDetail.getPrice() : BigDecimal.ZERO;

        return DriverOrderResponse.OrderItemResponse.builder()
                .foodName(cartDetail.getFood().getName())
                .quantity(cartDetail.getQuantity())
                .price(price)
                .note(cartDetail.getNote())
                .additions(new ArrayList<>()) // Có thể thêm logic để lấy additions
                .build();
    }

    /**
     * Lấy restaurant từ order
     */
    private Restaurant getRestaurantByOrder(Order order) {
        if (!order.getCartDetails().isEmpty()) {
            return order.getCartDetails().get(0).getFood().getRestaurant();
        } else {
            // Trả về restaurant rỗng nếu không có cart details
            return Restaurant.builder()
                    .name("Unknown Restaurant")
                    .phone("Unknown Phone")
                    .address(Address.builder()
                            .detail("Unknown Address")
                            .lat(0.0)
                            .lon(0.0)
                            .build())
                    .build();
        }
    }

    /**
     * Chuyển đổi Transaction entity thành TransactionResponse
     */
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .shipperId(transaction.getShipper().getId())
                .orderId(transaction.getOrderId())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .note(null) // Note field doesn't exist in Transaction entity
                .orderCode(transaction.getOrderId() != null ? "ORD" + transaction.getOrderId() : null)
                .customerName(null) // Có thể lấy từ Order nếu cần
                .restaurantName(null) // Có thể lấy từ Order nếu cần
                .transactionDate(transaction.getTransactionDate())
                .completedDate(transaction.getTransactionDate()) // Use transactionDate as completedDate
                .createdAt(transaction.getTransactionDate()) // Use transactionDate as createdAt
                .bankName(null) // Chưa có trong entity
                .bankAccountNumber(null)
                .transferReference(null)
                .balanceBefore(0L) // Có thể tính toán
                .balanceAfter(0L) // Có thể tính toán
                .paymentMethod("WALLET")
                .currency("VND")
                .isRefundable(false)
                .refundableUntil(null)
                .build();
    }

    // Method mới để tính delivery fees từ Orders
    private BigDecimal calculateDeliveryFeesFromOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getShippingFee() != null)
                .map(Order::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Method mới để tính tips từ Orders
    private BigDecimal calculateTipsFromOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getTipAmount() != null)
                .map(Order::getTipAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Method mới để tính shipper earnings từ Orders
    private BigDecimal calculateShipperEarningsFromOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getShipperEarning() != null)
                .map(Order::getShipperEarning)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Method mới để tính daily earnings từ Orders
    private Map<String, BigDecimal> calculateDailyEarningsFromOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getShipperEarning() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().toLocalDate().toString(),
                        Collectors.mapping(
                                Order::getShipperEarning,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }
}