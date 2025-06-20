package com.api.controller;

import com.api.dto.response.LocationDistanceResponse;
import com.api.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private MockMvc mockMvc;

    // Add test-specific exception handler
    @ControllerAdvice
    static class TestControllerAdvice {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(locationController)
                .setControllerAdvice(new TestControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should return location display name when valid coordinates are provided")
    void getLocation_WithValidCoordinates_ReturnsDisplayName() throws Exception {
        // Arrange
        double lat = 10.7769;
        double lon = 106.7009;
        String expectedDisplayName = "Ho Chi Minh City, Vietnam";

        when(locationService.getLocationDisplayName(lat, lon)).thenReturn(expectedDisplayName);

        // Act & Assert
        mockMvc.perform(get("/location")
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedDisplayName));

        verify(locationService).getLocationDisplayName(lat, lon);
    }

    @Test
    @DisplayName("Should return distance information when valid route coordinates are provided")
    void getRoutes_WithValidCoordinates_ReturnsDistanceInfo() throws Exception {
        // Arrange
        double lat1 = 10.7769;
        double lon1 = 106.7009;
        double lat2 = 10.8231;
        double lon2 = 106.6296;

        LocationDistanceResponse expectedResponse = LocationDistanceResponse.builder()
                .distance(10645.2)
                .duration(702.0)
                .shippingFee(new BigDecimal("25000"))
                .build();

        when(locationService.getDistance(eq(lat1), eq(lon1), eq(lat2), eq(lon2)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/location/routes")
                        .param("lat1", String.valueOf(lat1))
                        .param("lon1", String.valueOf(lon1))
                        .param("lat2", String.valueOf(lat2))
                        .param("lon2", String.valueOf(lon2)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"distance\":10645.2,\"duration\":702.0,\"shippingFee\":25000}"));

        verify(locationService).getDistance(lat1, lon1, lat2, lon2);
    }

    @Test
    @DisplayName("Should handle errors when location service throws exception")
    void getLocation_ServiceThrowsException_ReturnsErrorMessage() throws Exception {
        // Arrange
        double lat = 10.7769;
        double lon = 106.7009;
        String errorMessage = "KHONG KET NOI SERVER";

        when(locationService.getLocationDisplayName(anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/location")
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(errorMessage));

        verify(locationService).getLocationDisplayName(lat, lon);
    }

    @Test
    @DisplayName("Should handle errors when routes service throws exception")
    void getRoutes_ServiceThrowsException_ReturnsErrorMessage() throws Exception {
        // Arrange
        double lat1 = 10.7769;
        double lon1 = 106.7009;
        double lat2 = 10.8231;
        double lon2 = 106.6296;
        String errorMessage = "Error calling OSRM API";

        when(locationService.getDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/location/routes")
                        .param("lat1", String.valueOf(lat1))
                        .param("lon1", String.valueOf(lon1))
                        .param("lat2", String.valueOf(lat2))
                        .param("lon2", String.valueOf(lon2)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(errorMessage));

        verify(locationService).getDistance(lat1, lon1, lat2, lon2);
    }
}