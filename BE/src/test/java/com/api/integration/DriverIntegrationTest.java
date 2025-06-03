package com.api.integration;

import com.api.dto.request.DriverLocationUpdateRequest;
import com.api.dto.request.DriverRegistrationRequest;
import com.api.entity.Account;
import com.api.entity.Driver;
import com.api.repository.AccountRepository;
import com.api.repository.DriverRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho Driver APIs
 * Kiểm tra toàn bộ flow từ controller đến database
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Driver Integration Tests")
class DriverIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Driver testDriver;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        // Tạo test account
        testAccount = new Account();
        testAccount.setUsername("testdriver");
        testAccount.setEmail("testdriver@gmail.com");
        testAccount.setPassword("password123");
        testAccount.setRole("ROLE_SHIPPER");
        testAccount = accountRepository.save(testAccount);

        // Tạo test driver
        testDriver = new Driver();
        testDriver.setAccount(testAccount);
        testDriver.setFullName("Test Driver");
        testDriver.setPhoneNumber("0123456789");
        testDriver.setLicenseNumber("TEST123456");
        testDriver.setVehicleType("MOTORCYCLE");
        testDriver.setVehiclePlate("29A1-TEST");
        testDriver.setIsActive(true);
        testDriver.setIsAvailable(true);
        testDriver.setCurrentLatitude(BigDecimal.valueOf(10.762622));
        testDriver.setCurrentLongitude(BigDecimal.valueOf(106.660172));
        testDriver = driverRepository.save(testDriver);
    }

    @Test
    @DisplayName("Integration Test: Đăng ký driver mới")
    @WithMockUser(roles = "ADMIN")
    void testRegisterDriverIntegration() throws Exception {
        // Given
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setFullName("New Driver");
        request.setPhoneNumber("0987654321");
        request.setEmail("newdriver@gmail.com");
        request.setLicenseNumber("NEW123456");
        request.setVehicleType("CAR");
        request.setVehiclePlate("30B2-NEW");

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/api/driver/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Driver"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"))
                .andExpect(jsonPath("$.vehicleType").value("CAR"));

        // Verify database
        assert driverRepository.findByPhoneNumber("0987654321").isPresent();
    }

    @Test
    @DisplayName("Integration Test: Lấy thông tin driver profile")
    @WithMockUser(roles = "SHIPPER")
    void testGetDriverProfileIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/driver/profile/{driverId}", testDriver.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDriver.getId()))
                .andExpect(jsonPath("$.fullName").value("Test Driver"))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("Integration Test: Cập nhật vị trí driver")
    @WithMockUser(roles = "SHIPPER")
    void testUpdateDriverLocationIntegration() throws Exception {
        // Given
        DriverLocationUpdateRequest request = new DriverLocationUpdateRequest();
        request.setLatitude(BigDecimal.valueOf(10.800000));
        request.setLongitude(BigDecimal.valueOf(106.700000));

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(put("/api/driver/{driverId}/location", testDriver.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentLatitude").value(10.800000))
                .andExpect(jsonPath("$.currentLongitude").value(106.700000));

        // Verify database
        Driver updatedDriver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assert updatedDriver.getCurrentLatitude().compareTo(BigDecimal.valueOf(10.800000)) == 0;
        assert updatedDriver.getCurrentLongitude().compareTo(BigDecimal.valueOf(106.700000)) == 0;
    }

    @Test
    @DisplayName("Integration Test: Cập nhật trạng thái available")
    @WithMockUser(roles = "SHIPPER")
    void testUpdateDriverAvailabilityIntegration() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/driver/{driverId}/availability?available=false", testDriver.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));

        // Verify database
        Driver updatedDriver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assert !updatedDriver.getIsAvailable();
    }

    @Test
    @DisplayName("Integration Test: Lấy danh sách active drivers")
    @WithMockUser(roles = "ADMIN")
    void testGetActiveDriversIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/driver/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testDriver.getId()))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @DisplayName("Integration Test: Tìm drivers gần nhất")
    @WithMockUser(roles = "ADMIN")
    void testFindNearestDriversIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/driver/nearest")
                        .param("latitude", "10.762622")
                        .param("longitude", "106.660172")
                        .param("radiusKm", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Integration Test: Unauthorized access should return 401")
    void testUnauthorizedAccessIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/driver/profile/{driverId}", testDriver.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Integration Test: Wrong role should return 403")
    @WithMockUser(roles = "USER")
    void testWrongRoleAccessIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/driver/profile/{driverId}", testDriver.getId()))
                .andExpect(status().isForbidden());
    }
} 