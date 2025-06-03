package com.api.controller;

import com.api.dto.request.DriverLocationUpdateRequest;
import com.api.dto.request.OrderStatusUpdateRequest;
import com.api.dto.response.DriverResponse;
import com.api.dto.response.OrderResponse;
import com.api.service.DriverService;
import com.api.service.DriverWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests cho DriverController
 * Kiểm tra tất cả các API endpoints của driver/shipper
 */
@WebMvcTest(DriverController.class)
@DisplayName("Driver Controller Tests")
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @MockBean
    private DriverWebSocketService driverWebSocketService;

    @Autowired
    private ObjectMapper objectMapper;

    private DriverResponse mockDriverResponse;
    private OrderResponse mockOrderResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Tạo mock data
        mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);
        mockDriverResponse.setFullName("Nguyễn Văn A");
        mockDriverResponse.setPhoneNumber("0123456789");
        mockDriverResponse.setIsActive(true);
        mockDriverResponse.setIsAvailable(true);
        mockDriverResponse.setCurrentLatitude(BigDecimal.valueOf(10.762622));
        mockDriverResponse.setCurrentLongitude(BigDecimal.valueOf(106.660172));

        mockOrderResponse = new OrderResponse();
        mockOrderResponse.setId(1L);
        mockOrderResponse.setStatus("ASSIGNED");
        mockOrderResponse.setTotalAmount(BigDecimal.valueOf(150000));
    }

    @Test
    @DisplayName("Đăng ký driver mới - thành công")
    @WithMockUser(roles = "ADMIN")
    void testRegisterDriver_Success() throws Exception {
        // Given
        when(driverService.registerDriver(any())).thenReturn(mockDriverResponse);

        String driverJson = """
            {
                "fullName": "Nguyễn Văn A",
                "phoneNumber": "0123456789",
                "email": "nguyenvana@gmail.com",
                "licenseNumber": "ABC123456",
                "vehicleType": "MOTORCYCLE",
                "vehiclePlate": "29A1-12345"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/driver/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"));

        verify(driverService, times(1)).registerDriver(any());
    }

    @Test
    @DisplayName("Lấy thông tin profile driver - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testGetDriverProfile_Success() throws Exception {
        // Given
        Long driverId = 1L;
        when(driverService.getDriverById(driverId)).thenReturn(mockDriverResponse);

        // When & Then
        mockMvc.perform(get("/api/driver/profile/{driverId}", driverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(driverService, times(1)).getDriverById(driverId);
    }

    @Test
    @DisplayName("Cập nhật vị trí driver - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testUpdateDriverLocation_Success() throws Exception {
        // Given
        Long driverId = 1L;
        DriverLocationUpdateRequest request = new DriverLocationUpdateRequest();
        request.setLatitude(BigDecimal.valueOf(10.762622));
        request.setLongitude(BigDecimal.valueOf(106.660172));

        when(driverService.updateDriverLocation(eq(driverId), any())).thenReturn(mockDriverResponse);

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(put("/api/driver/{driverId}/location", driverId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentLatitude").value(10.762622))
                .andExpect(jsonPath("$.currentLongitude").value(106.660172));

        verify(driverService, times(1)).updateDriverLocation(eq(driverId), any());
    }

    @Test
    @DisplayName("Bật/tắt trạng thái available của driver - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testUpdateDriverAvailability_Success() throws Exception {
        // Given
        Long driverId = 1L;
        mockDriverResponse.setIsAvailable(false);
        
        when(driverService.updateDriverAvailability(driverId, false)).thenReturn(mockDriverResponse);

        // When & Then
        mockMvc.perform(put("/api/driver/{driverId}/availability?available=false", driverId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));

        verify(driverService, times(1)).updateDriverAvailability(driverId, false);
    }

    @Test
    @DisplayName("Lấy danh sách orders của driver - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testGetDriverOrders_Success() throws Exception {
        // Given
        Long driverId = 1L;
        List<OrderResponse> mockOrders = Arrays.asList(mockOrderResponse);
        
        when(driverService.getDriverOrders(driverId, "ASSIGNED")).thenReturn(mockOrders);

        // When & Then
        mockMvc.perform(get("/api/driver/{driverId}/orders?status=ASSIGNED", driverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("ASSIGNED"));

        verify(driverService, times(1)).getDriverOrders(driverId, "ASSIGNED");
    }

    @Test
    @DisplayName("Accept order - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testAcceptOrder_Success() throws Exception {
        // Given
        Long driverId = 1L;
        Long orderId = 1L;
        
        when(driverService.acceptOrder(driverId, orderId)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(post("/api/driver/{driverId}/orders/{orderId}/accept", driverId, orderId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));

        verify(driverService, times(1)).acceptOrder(driverId, orderId);
        verify(driverWebSocketService, times(1)).sendOrderUpdateToDriver(eq(driverId), any());
    }

    @Test
    @DisplayName("Cập nhật trạng thái order - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        Long driverId = 1L;
        Long orderId = 1L;
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus("PICKED_UP");
        request.setNotes("Đã lấy hàng từ nhà hàng");

        mockOrderResponse.setStatus("PICKED_UP");
        when(driverService.updateOrderStatus(eq(driverId), eq(orderId), any())).thenReturn(mockOrderResponse);

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(put("/api/driver/{driverId}/orders/{orderId}/status", driverId, orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PICKED_UP"));

        verify(driverService, times(1)).updateOrderStatus(eq(driverId), eq(orderId), any());
        verify(driverWebSocketService, times(1)).sendOrderUpdateToDriver(eq(driverId), any());
    }

    @Test
    @DisplayName("Lấy lịch sử earnings - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testGetDriverEarnings_Success() throws Exception {
        // Given
        Long driverId = 1L;
        BigDecimal totalEarnings = BigDecimal.valueOf(2500000);
        
        when(driverService.getDriverEarnings(driverId, "2024-01-01", "2024-01-31"))
                .thenReturn(totalEarnings);

        // When & Then
        mockMvc.perform(get("/api/driver/{driverId}/earnings?startDate=2024-01-01&endDate=2024-01-31", driverId))
                .andExpect(status().isOk())
                .andExpect(content().string("2500000"));

        verify(driverService, times(1)).getDriverEarnings(driverId, "2024-01-01", "2024-01-31");
    }

    @Test
    @DisplayName("Lấy danh sách orders available - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testGetAvailableOrders_Success() throws Exception {
        // Given
        Long driverId = 1L;
        List<OrderResponse> availableOrders = Arrays.asList(mockOrderResponse);
        
        when(driverService.getAvailableOrdersForDriver(driverId, 5.0)).thenReturn(availableOrders);

        // When & Then
        mockMvc.perform(get("/api/driver/{driverId}/available-orders?radiusKm=5.0", driverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(driverService, times(1)).getAvailableOrdersForDriver(driverId, 5.0);
    }

    @Test
    @DisplayName("Reject order - thành công")
    @WithMockUser(roles = "SHIPPER")
    void testRejectOrder_Success() throws Exception {
        // Given
        Long driverId = 1L;
        Long orderId = 1L;
        String reason = "Quá xa";

        // When & Then
        mockMvc.perform(post("/api/driver/{driverId}/orders/{orderId}/reject?reason={reason}", 
                        driverId, orderId, reason)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(driverService, times(1)).rejectOrder(driverId, orderId, reason);
    }

    @Test
    @DisplayName("Test authorization - chỉ ROLE_SHIPPER mới truy cập được")
    void testUnauthorizedAccess_ShouldReturn403() throws Exception {
        // When & Then - không có authentication
        mockMvc.perform(get("/api/driver/1/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test với role khác SHIPPER - should return 403")
    @WithMockUser(roles = "USER")
    void testWrongRole_ShouldReturn403() throws Exception {
        // When & Then - role USER thay vì SHIPPER
        mockMvc.perform(get("/api/driver/1/profile"))
                .andExpect(status().isForbidden());
    }
} 