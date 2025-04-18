package com.api.service;

import com.api.dto.response.LocationDistanceResponse;

public interface LocationService {
    String getLocationDisplayName(Double lat, Double lon);

    LocationDistanceResponse getDistance(Double lat1, Double lon1, Double lat2, Double lon2);
}
