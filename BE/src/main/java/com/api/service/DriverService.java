package com.api.service;

import com.api.dto.request.*;
import com.api.dto.response.*;
import com.api.entity.Shipper;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho các chức năng của Driver/Shipper
 * Xử lý logic nghiệp vụ liên quan đến shipper và đơn hàng
 */
public interface DriverService {
    
    // ===============================
    // AUTHENTICATION & PROFILE
    // ===============================
    
    /**
     * Đăng nhập cho shipper
     * @param request thông tin đăng nhập (phone/password)
     * @return thông tin shipper và JWT token
     */
    DriverLoginResponse login(DriverLoginRequest request);
    
    /**
     * Lấy thông tin shipper từ token
     * @param token JWT token
     * @return thông tin shipper
     */
    DriverLoginResponse getShipperFromToken(String token);
    
    /**
     * Lấy shipper ID từ số điện thoại
     * @param phone số điện thoại
     * @return shipper ID
     */
    Long getShipperIdByPhone(String phone);
    
    /**
     * Lấy thông tin shipper theo ID
     * @param shipperId ID shipper
     * @return thông tin shipper
     */
    Shipper getShipperById(Long shipperId);
    
    // ===============================
    // PROFILE MANAGEMENT (Phase 2)
    // ===============================
    
    /**
     * Lấy thông tin profile đầy đủ của shipper
     * @param shipperId ID shipper
     * @return thông tin profile
     */
    DriverLoginResponse getProfile(Long shipperId);
    
    /**
     * Cập nhật thông tin profile của shipper
     * @param shipperId ID shipper
     * @param request thông tin cập nhật
     */
    void updateProfile(Long shipperId, UpdateProfileRequest request);
    
    /**
     * Lấy thống kê profile của shipper
     * @param shipperId ID shipper
     * @return thống kê chi tiết
     */
    ProfileStatsResponse getProfileStats(Long shipperId);
    
    /**
     * Upload avatar cho shipper
     * @param shipperId ID shipper
     * @param imageUrl URL hình ảnh
     */
    void uploadAvatar(Long shipperId, String imageUrl);
    
    // ===============================
    // LOCATION & TRACKING
    // ===============================
    
    /**
     * Cập nhật vị trí shipper
     * @param shipperId ID shipper
     * @param request thông tin vị trí mới
     */
    void updateLocation(Long shipperId, UpdateLocationRequest request);
    
    /**
     * Lấy vị trí hiện tại của shipper
     * @param shipperId ID shipper
     * @return vị trí hiện tại
     */
    UpdateLocationRequest getCurrentLocation(Long shipperId);
    
    // ===============================
    // ORDER MANAGEMENT
    // ===============================
    
    /**
     * Lấy danh sách đơn hàng khả dụng cho shipper
     * @param shipperId ID shipper
     * @return danh sách đơn hàng có thể nhận
     */
    List<DriverOrderResponse> getAvailableOrders(Long shipperId);
    
    /**
     * Lấy danh sách đơn hàng đã được assign cho shipper
     * @param shipperId ID shipper
     * @return danh sách đơn hàng đã nhận
     */
    List<DriverOrderResponse> getAssignedOrders(Long shipperId);
    
    /**
     * Lấy lịch sử đơn hàng của shipper
     * @param shipperId ID shipper
     * @param pageable thông tin phân trang
     * @return lịch sử đơn hàng
     */
    List<DriverOrderResponse> getOrderHistory(Long shipperId, Pageable pageable);
    
    /**
     * Lấy chi tiết đơn hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     * @return chi tiết đơn hàng
     */
    DriverOrderResponse getOrderDetails(Long shipperId, Long orderId);
    
    /**
     * Chấp nhận đơn hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     * @param request thông tin bổ sung (thời gian dự kiến)
     */
    void acceptOrder(Long shipperId, Long orderId, OrderActionRequest request);
    
    /**
     * Từ chối đơn hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     * @param request thông tin từ chối (lý do)
     */
    void rejectOrder(Long shipperId, Long orderId, OrderActionRequest request);
    
    /**
     * Cập nhật trạng thái đơn hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     * @param status trạng thái mới
     * @param request thông tin bổ sung
     */
    void updateOrderStatus(Long shipperId, Long orderId, String status, OrderActionRequest request);
    
    /**
     * Xác nhận đã lấy hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     */
    void confirmPickup(Long shipperId, Long orderId);
    
    /**
     * Xác nhận đã giao hàng
     * @param shipperId ID shipper
     * @param orderId ID đơn hàng
     */
    void confirmDelivery(Long shipperId, Long orderId);
    
    /**
     * Lấy số lượng đơn hàng đang chờ phản hồi
     * @param shipperId ID shipper
     * @return số lượng đơn hàng chờ
     */
    Integer getPendingOrdersCount(Long shipperId);
    
    // ===============================
    // WALLET & FINANCIAL (Phase 2) 
    // ===============================
    
    /**
     * Lấy thông tin ví của shipper
     * @param shipperId ID shipper
     * @return thông tin ví
     */
    WalletResponse getWallet(Long shipperId);
    
    /**
     * Lấy lịch sử giao dịch của shipper
     * @param shipperId ID shipper
     * @param pageable thông tin phân trang
     * @return danh sách giao dịch
     */
    List<TransactionResponse> getTransactionHistory(Long shipperId, Pageable pageable);
    
    /**
     * Lấy giao dịch theo loại
     * @param shipperId ID shipper
     * @param type loại giao dịch
     * @return danh sách giao dịch
     */
    List<TransactionResponse> getTransactionsByType(Long shipperId, String type);
    
    /**
     * Rút tiền từ ví
     * @param shipperId ID shipper
     * @param request thông tin rút tiền
     */
    void withdrawMoney(Long shipperId, WithdrawRequest request);
    
    /**
     * Lấy thống kê thu nhập
     * @param shipperId ID shipper
     * @return thống kê thu nhập chi tiết
     */
    WalletResponse getEarningsStats(Long shipperId);
    
    /**
     * Kiểm tra số dư ví có đủ để rút không
     * @param shipperId ID shipper
     * @param amount số tiền muốn rút
     * @return true nếu đủ số dư
     */
    Boolean canWithdraw(Long shipperId, Long amount);
    
    // ===============================
    // REWARDS SYSTEM (Phase 3)
    // ===============================
    
    /**
     * Lấy danh sách phần thưởng có thể nhận
     * @param shipperId ID shipper
     * @return danh sách phần thưởng available
     */
    List<RewardResponse> getAvailableRewards(Long shipperId);
    
    /**
     * Lấy lịch sử phần thưởng đã nhận
     * @param shipperId ID shipper
     * @param pageable thông tin phân trang
     * @return lịch sử phần thưởng đã claim
     */
    List<RewardResponse> getClaimedRewards(Long shipperId, Pageable pageable);
    
    /**
     * Nhận phần thưởng
     * @param shipperId ID shipper
     * @param rewardId ID phần thưởng
     */
    void claimReward(Long shipperId, Long rewardId);
    
    /**
     * Xem tiến độ các phần thưởng
     * @param shipperId ID shipper
     * @return tiến độ hoàn thành các phần thưởng
     */
    List<RewardResponse> getRewardProgress(Long shipperId);
    
    // ===============================
    // ANALYTICS (Phase 3)
    // ===============================
    
    /**
     * Thống kê hiệu suất shipper
     * @param shipperId ID shipper
     * @param period kỳ thống kê (today, week, month, year)
     * @return thống kê hiệu suất chi tiết
     */
    AnalyticsResponse getPerformanceAnalytics(Long shipperId, String period);
    
    /**
     * Phân tích thu nhập
     * @param shipperId ID shipper
     * @param period kỳ phân tích
     * @return phân tích thu nhập chi tiết
     */
    AnalyticsResponse getEarningsAnalytics(Long shipperId, String period);
    
    /**
     * Thống kê đơn hàng
     * @param shipperId ID shipper
     * @param period kỳ thống kê
     * @return thống kê đơn hàng chi tiết
     */
    AnalyticsResponse getOrderAnalytics(Long shipperId, String period);
    
    // ===============================
    // SYSTEM UTILITIES (Phase 3)
    // ===============================
    
    /**
     * Kiểm tra phiên bản ứng dụng
     * @param currentVersion phiên bản hiện tại của app
     * @return thông tin phiên bản và cập nhật
     */
    SystemResponse checkAppVersion(String currentVersion);
    
    /**
     * Gửi phản hồi từ shipper
     * @param shipperId ID shipper
     * @param request thông tin feedback
     * @return thông tin feedback đã gửi
     */
    SystemResponse submitFeedback(Long shipperId, FeedbackRequest request);
    
    /**
     * Lấy thông tin hỗ trợ
     * @return thông tin liên hệ hỗ trợ, FAQ, v.v.
     */
    SystemResponse getSupportInfo();
} 