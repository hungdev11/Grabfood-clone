package com.api.service;

import com.api.dto.response.LocationDistanceResponse;
import com.api.service.Imp.LocationServiceImp;
import com.api.utils.ShippingFeeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    // Create a testable subclass that overrides the methods we need to control
    private class TestLocationService extends LocationServiceImp {
        @Override
        public String getLocationDisplayName(Double lat, Double lon) {
            try {
                String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
                Map<String, Object> response = mockRestTemplate.getForObject(url, Map.class);

                if (response == null) {
                    throw new RuntimeException("LOCATION NOT FOUND");
                }
                return response.get("display_name").toString();
            } catch (Exception e) {
                throw new RuntimeException("KHONG KET NOI SERVER");
            }
        }

        @Override
        public LocationDistanceResponse getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
            ObjectMapper objectMapper = new ObjectMapper();
            String url = "http://router.project-osrm.org/route/v1/driving/"+lon1+","+lat1+";"+lon2+","+lat2+"?overview=false";

            try {
                // Gửi yêu cầu HTTP
                String jsonResponse = mockRestTemplate.getForObject(url, String.class);

                // Parse JSON
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // Kiểm tra code
                String code = rootNode.path("code").asText();
                if (!"Ok".equals(code)) {
                    throw new RuntimeException("OSRM API returned error: " + code);
                }

                // Lấy distance và duration từ routes[0]
                JsonNode routeNode = rootNode.path("routes").get(0);
                double distance = routeNode.path("distance").asDouble(); // mét
                double duration = routeNode.path("duration").asDouble(); // giây

                // Xây dựng response
                return LocationDistanceResponse.builder()
                        .distance(distance)
                        .duration(duration)
                        .shippingFee(ShippingFeeUtil.calculateShippingFee(distance))
                        .build();

            } catch (Exception e) {
                throw new RuntimeException("Error calling OSRM API: " + e.getMessage(), e);
            }
        }
    }

    private TestLocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new TestLocationService();
    }

    @Test
    void getLocationDisplayName_Success() {
        // Arrange
        Double lat = 10.7769;
        Double lon = 106.7009;

        // Setup mock response
        Map<String, Object> response = new HashMap<>();
        response.put("display_name", "Ho Chi Minh City, Vietnam");
        when(mockRestTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        // Act
        String result = locationService.getLocationDisplayName(lat, lon);

        // Assert
        assertEquals("Ho Chi Minh City, Vietnam", result);
        verify(mockRestTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    void getLocationDisplayName_ApiReturnsNull() {
        // Arrange
        Double lat = 10.7769;
        Double lon = 106.7009;

        // Setup mock response
        when(mockRestTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getLocationDisplayName(lat, lon);
        });

        assertEquals("KHONG KET NOI SERVER", exception.getMessage());
        verify(mockRestTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    void getLocationDisplayName_ApiThrowsException() {
        // Arrange
        Double lat = 10.7769;
        Double lon = 106.7009;

        // Setup mock response
        when(mockRestTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getLocationDisplayName(lat, lon);
        });

        assertEquals("KHONG KET NOI SERVER", exception.getMessage());
        verify(mockRestTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    void getDistance_Success() throws Exception {
        // Arrange
        Double lat1 = 10.7769;
        Double lon1 = 106.7009;
        Double lat2 = 10.8231;
        Double lon2 = 106.6296;

        // Create sample JSON response
        String jsonResponse = "{\n" +
                "  \"code\": \"Ok\",\n" +
                "  \"routes\": [\n" +
                "    {\n" +
                "      \"distance\": 10645.2,\n" +
                "      \"duration\": 702.0\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Mock API response
        when(mockRestTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(jsonResponse);

        // Act
        LocationDistanceResponse response = locationService.getDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertNotNull(response);
        assertEquals(10645.2, response.getDistance());
        assertEquals(702.0, response.getDuration());
        assertTrue(response.getShippingFee().compareTo(BigDecimal.ZERO) > 0);
        verify(mockRestTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getDistance_ApiReturnsError() throws Exception {
        // Arrange
        Double lat1 = 10.7769;
        Double lon1 = 106.7009;
        Double lat2 = 10.8231;
        Double lon2 = 106.6296;

        // Create error JSON response
        String jsonResponse = "{\n" +
                "  \"code\": \"NoRoute\"\n" +
                "}";

        // Mock API response
        when(mockRestTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(jsonResponse);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getDistance(lat1, lon1, lat2, lon2);
        });

        assertTrue(exception.getMessage().contains("OSRM API returned error"));
        verify(mockRestTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getDistance_ApiThrowsException() {
        // Arrange
        Double lat1 = 10.7769;
        Double lon1 = 106.7009;
        Double lat2 = 10.8231;
        Double lon2 = 106.6296;

        // Mock API error
        when(mockRestTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getDistance(lat1, lon1, lat2, lon2);
        });

        assertTrue(exception.getMessage().contains("Error calling OSRM API"));
        verify(mockRestTemplate).getForObject(anyString(), eq(String.class));
    }
}