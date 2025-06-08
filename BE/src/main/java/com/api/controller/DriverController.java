package com.api.controller;

import com.api.dto.request.DriverLoginRequest;
import com.api.dto.request.OrderActionRequest;
import com.api.dto.request.UpdateLocationRequest;
import com.api.dto.request.UpdateProfileRequest;
import com.api.dto.request.WithdrawRequest;
import com.api.dto.request.FeedbackRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.DriverLoginResponse;
import com.api.dto.response.DriverOrderResponse;
import com.api.dto.response.ProfileStatsResponse;
import com.api.dto.response.TransactionResponse;
import com.api.dto.response.WalletResponse;
import com.api.dto.response.AnalyticsResponse;
import com.api.dto.response.RewardResponse;
import com.api.dto.response.SystemResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.service.DriverService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverService driverService;
    private final JwtService jwtService;

    // ===============================
    // AUTHENTICATION APIs
    // ===============================

    /**
     * API đăng nhập cho driver
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DriverLoginResponse>> login(@Valid @RequestBody DriverLoginRequest request) {
        log.info("Driver login attempt for phone: {}", request.getPhone());

        try {
            DriverLoginResponse response = driverService.login(request);

            return ResponseEntity.ok(ApiResponse.<DriverLoginResponse>builder()
                    .code(200)
                    .message("Đăng nhập thành công")
                    .data(response)
                    .build());

        } catch (AppException e) {
            log.warn("Driver login failed for phone: {} - {}", request.getPhone(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<DriverLoginResponse>builder()
                    .code(400)
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * API đăng xuất cho driver
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        log.info("Driver {} logged out", shipperId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Đăng xuất thành công")
                .build());
    }

    /**
     * API verify token
     */
    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<DriverLoginResponse>> verifyToken(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        DriverLoginResponse response = driverService.getShipperFromToken(getTokenFromRequest(request));

        return ResponseEntity.ok(ApiResponse.<DriverLoginResponse>builder()
                .code(200)
                .message("Token hợp lệ")
                .data(response)
                .build());
    }

    // ===============================
    // LOCATION & TRACKING APIs
    // ===============================

    /**
     * Cập nhật vị trí của shipper
     */
    @PostMapping("/location/update")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            HttpServletRequest request,
            @Valid @RequestBody UpdateLocationRequest locationRequest) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.updateLocation(shipperId, locationRequest);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Cập nhật vị trí thành công")
                .build());
    }

    /**
     * API lấy vị trí hiện tại của driver
     */
    @GetMapping("/location/current")
    public ResponseEntity<ApiResponse<UpdateLocationRequest>> getCurrentLocation(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        UpdateLocationRequest location = driverService.getCurrentLocation(shipperId);

        return ResponseEntity.ok(ApiResponse.<UpdateLocationRequest>builder()
                .code(200)
                .message("Lấy vị trí thành công")
                .data(location)
                .build());
    }

    // ===============================
    // ORDER MANAGEMENT APIs
    // ===============================

    /**
     * API lấy danh sách đơn hàng có sẵn cho driver
     */
    @GetMapping("/orders/available")
    public ResponseEntity<ApiResponse<List<DriverOrderResponse>>> getAvailableOrders(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        List<DriverOrderResponse> orders = driverService.getAvailableOrders(shipperId);

        return ResponseEntity.ok(ApiResponse.<List<DriverOrderResponse>>builder()
                .code(200)
                .message("Lấy danh sách đơn hàng thành công")
                .data(orders)
                .build());
    }

    /**
     * API lấy danh sách đơn hàng được assign cho driver
     */
    @GetMapping("/orders/assigned")
    public ResponseEntity<ApiResponse<List<DriverOrderResponse>>> getAssignedOrders(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        List<DriverOrderResponse> orders = driverService.getAssignedOrders(shipperId);

        return ResponseEntity.ok(ApiResponse.<List<DriverOrderResponse>>builder()
                .code(200)
                .message("Lấy đơn hàng được giao thành công")
                .data(orders)
                .build());
    }

    /**
     * API lấy lịch sử đơn hàng của driver
     */
    @GetMapping("/orders/history")
    public ResponseEntity<ApiResponse<List<DriverOrderResponse>>> getOrderHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long shipperId = getShipperIdFromToken(request);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        List<DriverOrderResponse> orders = driverService.getOrderHistory(shipperId, pageable);

        return ResponseEntity.ok(ApiResponse.<List<DriverOrderResponse>>builder()
                .code(200)
                .message("Lấy lịch sử đơn hàng thành công")
                .data(orders)
                .build());
    }

    /**
     * API lấy chi tiết đơn hàng
     */
    @GetMapping("/orders/{orderId}/details")
    public ResponseEntity<ApiResponse<DriverOrderResponse>> getOrderDetails(
            HttpServletRequest request,
            @PathVariable Long orderId) {

        Long shipperId = getShipperIdFromToken(request);

        DriverOrderResponse order = driverService.getOrderDetails(shipperId, orderId);

        return ResponseEntity.ok(ApiResponse.<DriverOrderResponse>builder()
                .code(200)
                .message("Lấy chi tiết đơn hàng thành công")
                .data(order)
                .build());
    }

    /**
     * API chấp nhận đơn hàng
     */
    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptOrder(
            HttpServletRequest request,
            @PathVariable Long orderId,
            @RequestBody(required = false) OrderActionRequest actionRequest) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.acceptOrder(shipperId, orderId, actionRequest);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Chấp nhận đơn hàng thành công")
                .build());
    }

    /**
     * API từ chối đơn hàng
     */
    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectOrder(
            HttpServletRequest request,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderActionRequest actionRequest) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.rejectOrder(shipperId, orderId, actionRequest);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Từ chối đơn hàng thành công")
                .build());
    }

    /**
     * API cập nhật trạng thái đơn hàng
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            HttpServletRequest request,
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestBody(required = false) OrderActionRequest actionRequest) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.updateOrderStatus(shipperId, orderId, status, actionRequest);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .build());
    }

    /**
     * API xác nhận đã lấy hàng
     */
    @PostMapping("/orders/{orderId}/pickup-confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPickup(
            HttpServletRequest request,
            @PathVariable Long orderId) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.confirmPickup(shipperId, orderId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Xác nhận lấy hàng thành công")
                .build());
    }

    /**
     * API xác nhận đã giao hàng
     */
    @PostMapping("/orders/{orderId}/delivery-confirm")
    public ResponseEntity<ApiResponse<Void>> confirmDelivery(
            HttpServletRequest request,
            @PathVariable Long orderId) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.confirmDelivery(shipperId, orderId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Xác nhận giao hàng thành công")
                .build());
    }

    /**
     * API lấy số đơn hàng đang chờ
     */
    @GetMapping("/orders/pending-count")
    public ResponseEntity<ApiResponse<Integer>> getPendingOrdersCount(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        Integer count = driverService.getPendingOrdersCount(shipperId);

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .code(200)
                .message("Lấy số đơn hàng chờ thành công")
                .data(count)
                .build());
    }

    // ===============================
    // PROFILE MANAGEMENT APIs (Phase 2)
    // ===============================

    /**
     * API lấy thông tin profile đầy đủ
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DriverLoginResponse>> getProfile(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        DriverLoginResponse profile = driverService.getProfile(shipperId);

        return ResponseEntity.ok(ApiResponse.<DriverLoginResponse>builder()
                .code(200)
                .message("Lấy thông tin profile thành công")
                .data(profile)
                .build());
    }

    /**
     * API cập nhật thông tin profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateProfileRequest updateRequest) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.updateProfile(shipperId, updateRequest);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Cập nhật profile thành công")
                .build());
    }

    /**
     * API lấy thống kê profile
     */
    @GetMapping("/profile/stats")
    public ResponseEntity<ApiResponse<ProfileStatsResponse>> getProfileStats(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        ProfileStatsResponse stats = driverService.getProfileStats(shipperId);

        return ResponseEntity.ok(ApiResponse.<ProfileStatsResponse>builder()
                .code(200)
                .message("Lấy thống kê profile thành công")
                .data(stats)
                .build());
    }

    /**
     * API upload avatar
     */
    @PostMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<Void>> uploadAvatar(
            HttpServletRequest request,
            @RequestParam String imageUrl) {

        Long shipperId = getShipperIdFromToken(request);

        driverService.uploadAvatar(shipperId, imageUrl);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Cập nhật avatar thành công")
                .build());
    }

    // ===============================
    // WALLET & FINANCIAL APIs (Phase 2)
    // ===============================

    /**
     * API lấy thông tin ví
     */
    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        WalletResponse wallet = driverService.getWallet(shipperId);

        return ResponseEntity.ok(ApiResponse.<WalletResponse>builder()
                .code(200)
                .message("Lấy thông tin ví thành công")
                .data(wallet)
                .build());
    }

    /**
     * API lấy lịch sử giao dịch
     */
    @GetMapping("/wallet/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long shipperId = getShipperIdFromToken(request);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        List<TransactionResponse> transactions = driverService.getTransactionHistory(shipperId, pageable);

        return ResponseEntity.ok(ApiResponse.<List<TransactionResponse>>builder()
                .code(200)
                .message("Lấy lịch sử giao dịch thành công")
                .data(transactions)
                .build());
    }

    /**
     * API lấy giao dịch theo loại
     */
    @GetMapping("/wallet/transactions/type")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByType(
            HttpServletRequest request,
            @RequestParam String type) {

        Long shipperId = getShipperIdFromToken(request);

        List<TransactionResponse> transactions = driverService.getTransactionsByType(shipperId, type);

        return ResponseEntity.ok(ApiResponse.<List<TransactionResponse>>builder()
                .code(200)
                .message("Lấy giao dịch theo loại thành công")
                .data(transactions)
                .build());
    }

    /**
     * API rút tiền
     */
    @PostMapping("/wallet/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMoney(
            HttpServletRequest request,
            @Valid @RequestBody WithdrawRequest withdrawRequest) {

        Long shipperId = getShipperIdFromToken(request);

        try {
            driverService.withdrawMoney(shipperId, withdrawRequest);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(200)
                    .message("Yêu cầu rút tiền đã được tạo thành công")
                    .build());

        } catch (AppException e) {
            log.warn("Withdrawal failed for shipper {}: {}", shipperId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .code(400)
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * API lấy thống kê thu nhập
     */
    @GetMapping("/wallet/earnings")
    public ResponseEntity<ApiResponse<WalletResponse>> getEarningsStats(HttpServletRequest request) {
        Long shipperId = getShipperIdFromToken(request);

        WalletResponse earnings = driverService.getEarningsStats(shipperId);

        return ResponseEntity.ok(ApiResponse.<WalletResponse>builder()
                .code(200)
                .message("Lấy thống kê thu nhập thành công")
                .data(earnings)
                .build());
    }

    /**
     * API kiểm tra khả năng rút tiền
     */
    @GetMapping("/wallet/can-withdraw")
    public ResponseEntity<ApiResponse<Boolean>> canWithdraw(
            HttpServletRequest request,
            @RequestParam Long amount) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            Boolean canWithdraw = driverService.canWithdraw(shipperId, amount);
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .code(200)
                    .message("Kiểm tra thành công")
                    .data(canWithdraw)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi kiểm tra khả năng rút tiền: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Boolean>builder()
                    .code(400)
                    .message("Lỗi kiểm tra rút tiền: " + e.getMessage())
                    .build());
        }
    }

    // ===============================
    // REWARDS SYSTEM APIs (Phase 3)
    // ===============================

    /**
     * Lấy danh sách phần thưởng khả dụng
     */
    @GetMapping("/rewards/available")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getAvailableRewards(HttpServletRequest request) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            List<RewardResponse> rewards = driverService.getAvailableRewards(shipperId);
            return ResponseEntity.ok(ApiResponse.<List<RewardResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách phần thưởng thành công")
                    .data(rewards)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách phần thưởng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<List<RewardResponse>>builder()
                    .code(400)
                    .message("Lỗi lấy phần thưởng: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Lấy lịch sử phần thưởng đã nhận
     */
    @GetMapping("/rewards/claimed")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getClaimedRewards(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            Pageable pageable = PageRequest.of(page, size);
            List<RewardResponse> rewards = driverService.getClaimedRewards(shipperId, pageable);
            return ResponseEntity.ok(ApiResponse.<List<RewardResponse>>builder()
                    .code(200)
                    .message("Lấy lịch sử phần thưởng thành công")
                    .data(rewards)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy lịch sử phần thưởng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<List<RewardResponse>>builder()
                    .code(400)
                    .message("Lỗi lấy lịch sử: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Nhận phần thưởng
     */
    @PostMapping("/rewards/{rewardId}/claim")
    public ResponseEntity<ApiResponse<Void>> claimReward(
            HttpServletRequest request,
            @PathVariable Long rewardId) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            driverService.claimReward(shipperId, rewardId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(200)
                    .message("Nhận phần thưởng thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi nhận phần thưởng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .code(400)
                    .message("Lỗi nhận thưởng: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Xem tiến độ các phần thưởng
     */
    @GetMapping("/rewards/progress")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getRewardProgress(HttpServletRequest request) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            List<RewardResponse> progress = driverService.getRewardProgress(shipperId);
            return ResponseEntity.ok(ApiResponse.<List<RewardResponse>>builder()
                    .code(200)
                    .message("Lấy tiến độ phần thưởng thành công")
                    .data(progress)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy tiến độ phần thưởng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<List<RewardResponse>>builder()
                    .code(400)
                    .message("Lỗi lấy tiến độ: " + e.getMessage())
                    .build());
        }
    }

    // ===============================
    // ANALYTICS APIs (Phase 3)
    // ===============================

    /**
     * Thống kê hiệu suất shipper
     */
    @GetMapping("/analytics/performance")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getPerformanceAnalytics(
            HttpServletRequest request,
            @RequestParam(defaultValue = "week") String period) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            AnalyticsResponse analytics = driverService.getPerformanceAnalytics(shipperId, period);
            return ResponseEntity.ok(ApiResponse.<AnalyticsResponse>builder()
                    .code(200)
                    .message("Lấy thống kê hiệu suất thành công")
                    .data(analytics)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy thống kê hiệu suất: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<AnalyticsResponse>builder()
                    .code(400)
                    .message("Lỗi thống kê: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Phân tích thu nhập
     */
    @GetMapping("/analytics/earnings")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getEarningsAnalytics(
            HttpServletRequest request,
            @RequestParam(defaultValue = "month") String period) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            AnalyticsResponse analytics = driverService.getEarningsAnalytics(shipperId, period);
            return ResponseEntity.ok(ApiResponse.<AnalyticsResponse>builder()
                    .code(200)
                    .message("Phân tích thu nhập thành công")
                    .data(analytics)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi phân tích thu nhập: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<AnalyticsResponse>builder()
                    .code(400)
                    .message("Lỗi phân tích: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Thống kê đơn hàng
     */
    @GetMapping("/analytics/orders")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getOrderAnalytics(
            HttpServletRequest request,
            @RequestParam(defaultValue = "week") String period) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            AnalyticsResponse analytics = driverService.getOrderAnalytics(shipperId, period);
            return ResponseEntity.ok(ApiResponse.<AnalyticsResponse>builder()
                    .code(200)
                    .message("Thống kê đơn hàng thành công")
                    .data(analytics)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi thống kê đơn hàng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<AnalyticsResponse>builder()
                    .code(400)
                    .message("Lỗi thống kê: " + e.getMessage())
                    .build());
        }
    }

    // ===============================
    // SYSTEM UTILITIES APIs (Phase 3)
    // ===============================

    /**
     * Kiểm tra phiên bản ứng dụng
     */
    @GetMapping("/system/version")
    public ResponseEntity<ApiResponse<SystemResponse>> checkAppVersion(
            @RequestParam String currentVersion) {
        try {
            SystemResponse versionInfo = driverService.checkAppVersion(currentVersion);
            return ResponseEntity.ok(ApiResponse.<SystemResponse>builder()
                    .code(200)
                    .message("Kiểm tra phiên bản thành công")
                    .data(versionInfo)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi kiểm tra phiên bản: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<SystemResponse>builder()
                    .code(400)
                    .message("Lỗi kiểm tra: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Gửi phản hồi từ shipper
     */
    @PostMapping("/system/feedback")
    public ResponseEntity<ApiResponse<SystemResponse>> submitFeedback(
            HttpServletRequest request,
            @Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            Long shipperId = getShipperIdFromToken(request);
            SystemResponse feedbackResponse = driverService.submitFeedback(shipperId, feedbackRequest);
            return ResponseEntity.ok(ApiResponse.<SystemResponse>builder()
                    .code(200)
                    .message("Gửi phản hồi thành công")
                    .data(feedbackResponse)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi gửi phản hồi: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<SystemResponse>builder()
                    .code(400)
                    .message("Lỗi gửi feedback: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Lấy thông tin hỗ trợ
     */
    @GetMapping("/system/support")
    public ResponseEntity<ApiResponse<SystemResponse>> getSupportInfo() {
        try {
            SystemResponse supportInfo = driverService.getSupportInfo();
            return ResponseEntity.ok(ApiResponse.<SystemResponse>builder()
                    .code(200)
                    .message("Lấy thông tin hỗ trợ thành công")
                    .data(supportInfo)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy thông tin hỗ trợ: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<SystemResponse>builder()
                    .code(400)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build());
        }
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    /**
     * Lấy shipper ID từ JWT token
     */
    private Long getShipperIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Token không hợp lệ");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        return driverService.getShipperIdByPhone(username);
    }

    /**
     * Lấy token từ request
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Token không hợp lệ");
        }
        return authHeader.substring(7);
    }
}