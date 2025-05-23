package com.api.service.Imp;

import com.api.dto.response.LocationDistanceResponse;
import com.api.service.LocationService;
import com.api.utils.ShippingFeeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class LocationServiceImp implements LocationService {
    @Override
    public String getLocationDisplayName(Double lat, Double lon) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

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
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String url = "http://router.project-osrm.org/route/v1/driving/"+lon1+","+lat1+";"+lon2+","+lat2+"?overview=false";

        try {
            // Gửi yêu cầu HTTP
            String jsonResponse = restTemplate.getForObject(url, String.class);

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

//            String from = getLocationDisplayName(lat1, lon1);
//            String to = getLocationDisplayName(lat2, lon2);
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
