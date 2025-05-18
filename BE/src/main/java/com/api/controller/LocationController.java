package com.api.controller;

import com.api.dto.response.LocationDistanceResponse;
import com.api.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping()
    public String getLocation(@RequestParam double lat, @RequestParam double lon) {
        return locationService.getLocationDisplayName(lat,lon);
    }

    @GetMapping("/routes")
    public LocationDistanceResponse getRoutes(@RequestParam double lat1, @RequestParam double lon1,@RequestParam double lat2, @RequestParam double lon2)
    {
        return locationService.getDistance(lat1,lon1,lat2,lon2);
    }
}
