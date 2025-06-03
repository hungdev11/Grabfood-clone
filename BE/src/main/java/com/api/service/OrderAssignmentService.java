package com.api.service;

import com.api.entity.Order;
import com.api.entity.Shipper;
import java.util.List;

/**
 * Service xử lý logic gán đơn hàng cho shipper thông minh
 * Giải quyết các vấn đề:
 * - Auto-assignment khi order chuyển sang PROCESSING
 * - Retry logic khi shipper reject
 * - Cooldown system cho rejected shippers
 * - Edge cases handling
 */
public interface OrderAssignmentService {

    /**
     * Tự động gán đơn hàng cho shipper phù hợp nhất
     * 
     * @param orderId ID của đơn hàng cần assign
     * @return true nếu assign thành công, false nếu không tìm được shipper
     */
    boolean assignOrderToOptimalShipper(Long orderId);

    /**
     * Tìm shipper tối ưu cho đơn hàng (theo distance, rating, acceptance rate)
     * 
     * @param order              Đơn hàng cần assign
     * @param excludedShipperIds Danh sách shipper đã reject (để loại trừ)
     * @return Shipper tối ưu nhất, null nếu không tìm được
     */
    Shipper findOptimalShipperForOrder(Order order, List<Long> excludedShipperIds);

    /**
     * Tìm danh sách shipper eligible cho đơn hàng (đã loại trừ cooldown)
     * 
     * @param order              Đơn hàng
     * @param excludedShipperIds Shipper đã reject
     * @return Danh sách shipper có thể assign
     */
    List<Shipper> findEligibleShippers(Order order, List<Long> excludedShipperIds);

    /**
     * Xử lý khi shipper reject đơn hàng - tự động tìm shipper khác
     * 
     * @param orderId           ID đơn hàng bị reject
     * @param rejectedShipperId ID shipper vừa reject
     * @return true nếu tìm được shipper khác, false nếu không
     */
    boolean handleOrderRejection(Long orderId, Long rejectedShipperId);

    /**
     * Xử lý khi shipper không phản hồi trong 60s (timeout)
     * 
     * @param orderId          ID đơn hàng bị timeout
     * @param timeoutShipperId ID shipper bị timeout
     * @return true nếu tìm được shipper khác, false nếu không
     */
    boolean handleOrderTimeout(Long orderId, Long timeoutShipperId);

    /**
     * Xử lý edge case khi không có shipper available
     * 
     * @param orderId ID đơn hàng không tìm được shipper
     */
    void handleNoShipperAvailable(Long orderId);

    /**
     * Kiểm tra shipper có trong cooldown period không
     * 
     * @param shipperId ID shipper
     * @param orderId   ID đơn hàng
     * @return true nếu đang trong cooldown, false nếu có thể assign
     */
    boolean isShipperInCooldown(Long shipperId, Long orderId);

    /**
     * Tạo assignment mới và gửi notification cho shipper
     * 
     * @param order   Đơn hàng
     * @param shipper Shipper được assign
     * @return Assignment được tạo
     */
    com.api.entity.OrderAssignment createAssignmentWithNotification(Order order, Shipper shipper);

    /**
     * Lấy danh sách shipper đã reject đơn hàng này
     * 
     * @param orderId ID đơn hàng
     * @return Danh sách ID shipper đã reject
     */
    List<Long> getRejectedShipperIds(Long orderId);

    /**
     * Schedule timeout check cho assignment (60 giây)
     * 
     * @param orderId   ID đơn hàng
     * @param shipperId ID shipper được assign
     */
    void scheduleTimeoutCheck(Long orderId, Long shipperId);

    /**
     * Cancel timeout check khi shipper đã accept/reject
     * 
     * @param orderId   ID đơn hàng
     * @param shipperId ID shipper
     */
    void cancelTimeoutCheck(Long orderId, Long shipperId);
}