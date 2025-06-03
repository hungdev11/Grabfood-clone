package com.api.service;

import com.api.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service xử lý WebSocket notifications cho Driver/Shipper
 * Gửi thông báo real-time về đơn hàng mới và cập nhật trạng thái đơn hàng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo đơn hàng mới cho tất cả driver trong khu vực
     * @param areaId ID khu vực
     * @param orderResponse thông tin đơn hàng
     */
    public void sendNewOrderToDriversInArea(Long areaId, OrderResponse orderResponse) {
        try {
            String topic = "/topic/drivers/area/" + areaId;
            log.info("Sending new order notification to drivers in area {}: Order ID {}", 
                    areaId, orderResponse.getId());
            
            messagingTemplate.convertAndSend(topic, orderResponse);
        } catch (Exception e) {
            log.error("Error sending new order notification to drivers in area {}", areaId, e);
        }
    }

    /**
     * Gửi thông báo cập nhật đơn hàng cho driver cụ thể
     * @param driverId ID của driver
     * @param orderResponse thông tin đơn hàng đã cập nhật
     */
    public void sendOrderUpdateToDriver(Long driverId, OrderResponse orderResponse) {
        try {
            String topic = "/topic/driver/" + driverId;
            log.info("Sending order update to driver {}: Order ID {} - Status {}", 
                    driverId, orderResponse.getId(), orderResponse.getStatus());
            
            messagingTemplate.convertAndSend(topic, orderResponse);
        } catch (Exception e) {
            log.error("Error sending order update to driver {}", driverId, e);
        }
    }

    /**
     * Gửi thông báo hủy đơn hàng cho driver
     * @param driverId ID của driver
     * @param orderId ID đơn hàng bị hủy
     * @param reason lý do hủy đơn
     */
    public void sendOrderCancellationToDriver(Long driverId, Long orderId, String reason) {
        try {
            String topic = "/topic/driver/" + driverId;
            
            // Tạo object thông báo hủy đơn
            var cancellationNotification = new Object() {
                public final String type = "ORDER_CANCELLED";
                public final Long cancelledOrderId = orderId;
                public final String cancellationReason = reason;
                public final long timestamp = System.currentTimeMillis();
            };
            
            log.info("Sending order cancellation to driver {}: Order ID {} - Reason: {}", 
                    driverId, orderId, reason);
            
            messagingTemplate.convertAndSend(topic, cancellationNotification);
        } catch (Exception e) {
            log.error("Error sending order cancellation to driver {}", driverId, e);
        }
    }

    /**
     * Gửi thông báo trạng thái kết nối cho driver
     * @param driverId ID của driver
     * @param isOnline trạng thái online/offline
     */
    public void sendDriverStatusUpdate(Long driverId, boolean isOnline) {
        try {
            String topic = "/topic/driver/" + driverId + "/status";
            
            var statusUpdate = new Object() {
                public final String type = "STATUS_UPDATE";
                public final boolean onlineStatus = isOnline;
                public final long timestamp = System.currentTimeMillis();
            };
            
            log.info("Sending status update to driver {}: {}", driverId, isOnline ? "ONLINE" : "OFFLINE");
            
            messagingTemplate.convertAndSend(topic, statusUpdate);
        } catch (Exception e) {
            log.error("Error sending status update to driver {}", driverId, e);
        }
    }

    /**
     * Gửi thông báo emergency broadcast cho tất cả driver
     * @param message nội dung thông báo khẩn cấp
     */
    public void sendEmergencyBroadcast(String message) {
        try {
            String topic = "/topic/drivers/emergency";
            
            var emergencyNotification = new Object() {
                public final String type = "EMERGENCY";
                public final String content = message;
                public final long timestamp = System.currentTimeMillis();
            };
            
            log.warn("Sending emergency broadcast to all drivers: {}", message);
            
            messagingTemplate.convertAndSend(topic, emergencyNotification);
        } catch (Exception e) {
            log.error("Error sending emergency broadcast", e);
        }
    }
} 