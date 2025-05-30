package com.api.controller;

import com.api.dto.request.UpdateShipperLocationRequest;
import com.api.dto.request.UpdateShipperProfileRequest;
import com.api.dto.request.UpdateShipperStatusRequest;
import com.api.dto.response.ShipperProfileResponse;
import com.api.entity.Shipper;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.service.ShipperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shippers")
@RequiredArgsConstructor
@Slf4j
public class ShipperController {

    private final ShipperService shipperService;

    @GetMapping("/profile")
    public ResponseEntity<ShipperProfileResponse> getShipperProfile() {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting shipper profile for: {}", phone);

            Shipper shipper = shipperService.getAuthenticatedShipperProfile(phone);

            // Build response
            ShipperProfileResponse response = ShipperProfileResponse.builder()
                    .id(shipper.getId())
                    .name(shipper.getName())
                    .phone(shipper.getPhone())
                    .email(shipper.getEmail())
                    .isOnline(shipper.getIsOnline())
                    .status(shipper.getStatus().toString())
                    .rating(shipper.getRating())
                    .totalOrders(shipper.getTotalOrders())
                    .completedOrders(shipper.getCompletedOrders())
                    .acceptanceRate(shipper.getAcceptanceRate())
                    .cancellationRate(shipper.getCancellationRate())
                    .totalEarnings(shipper.getTotalEarnings())
                    .gems(shipper.getGems())
                    .vehicleType(shipper.getVehicleType())
                    .vehicleNumber(shipper.getVehicleNumber())
                    .licensePlate(shipper.getLicensePlate())
                    .currentLatitude(shipper.getCurrentLatitude())
                    .currentLongitude(shipper.getCurrentLongitude())
                    .createdDate(shipper.getCreatedDate())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AppException e) {
            log.error("Error getting shipper profile: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getErrorCode().getStatusCode().value()).build();
        } catch (Exception e) {
            log.error("Unexpected error getting shipper profile", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ShipperProfileResponse> updateShipperProfile(
            @RequestBody UpdateShipperProfileRequest request) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Updating shipper profile for: {}", phone);

            // Get shipper ID
            Shipper currentShipper = shipperService.getAuthenticatedShipperProfile(phone);

            // Update profile
            Shipper updatedShipper = shipperService.updateShipperProfile(currentShipper.getId(), request);

            // Build response
            ShipperProfileResponse response = ShipperProfileResponse.builder()
                    .id(updatedShipper.getId())
                    .name(updatedShipper.getName())
                    .phone(updatedShipper.getPhone())
                    .email(updatedShipper.getEmail())
                    .isOnline(updatedShipper.getIsOnline())
                    .status(updatedShipper.getStatus().toString())
                    .rating(updatedShipper.getRating())
                    .totalOrders(updatedShipper.getTotalOrders())
                    .completedOrders(updatedShipper.getCompletedOrders())
                    .acceptanceRate(updatedShipper.getAcceptanceRate())
                    .cancellationRate(updatedShipper.getCancellationRate())
                    .totalEarnings(updatedShipper.getTotalEarnings())
                    .gems(updatedShipper.getGems())
                    .vehicleType(updatedShipper.getVehicleType())
                    .vehicleNumber(updatedShipper.getVehicleNumber())
                    .licensePlate(updatedShipper.getLicensePlate())
                    .currentLatitude(updatedShipper.getCurrentLatitude())
                    .currentLongitude(updatedShipper.getCurrentLongitude())
                    .createdDate(updatedShipper.getCreatedDate())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AppException e) {
            log.error("Error updating shipper profile: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getErrorCode().getStatusCode().value()).build();
        } catch (Exception e) {
            log.error("Unexpected error updating shipper profile", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateShipperStatus(@RequestBody UpdateShipperStatusRequest request) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Updating shipper status for: {} to online: {}", phone, request.getIsOnline());

            // Get shipper ID
            Shipper currentShipper = shipperService.getAuthenticatedShipperProfile(phone);

            // Update online status
            Shipper updatedShipper = shipperService.updateShipperOnlineStatus(
                    currentShipper.getId(), request.getIsOnline());

            return ResponseEntity.ok().body("{\n" +
                    "  \"message\": \"Status updated successfully\",\n" +
                    "  \"isOnline\": " + updatedShipper.getIsOnline() + ",\n" +
                    "  \"shipperId\": " + updatedShipper.getId() + "\n" +
                    "}");
        } catch (AppException e) {
            log.error("Error updating shipper status: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getErrorCode().getStatusCode().value())
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error updating shipper status", e);
            return ResponseEntity.status(500)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }

    @PutMapping("/location")
    public ResponseEntity<?> updateShipperLocation(@RequestBody UpdateShipperLocationRequest request) {
        try {
            // Validate request
            if (request.getLatitude() == null || request.getLongitude() == null) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Latitude and longitude are required\"}");
            }

            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Updating shipper location for: {} to lat: {}, lng: {}",
                    phone, request.getLatitude(), request.getLongitude());

            // Get shipper ID
            Shipper currentShipper = shipperService.getAuthenticatedShipperProfile(phone);

            // Update location
            Shipper updatedShipper = shipperService.updateShipperLocation(
                    currentShipper.getId(), request.getLatitude(), request.getLongitude());

            return ResponseEntity.ok().body("{\n" +
                    "  \"message\": \"Location updated successfully\",\n" +
                    "  \"latitude\": " + updatedShipper.getCurrentLatitude() + ",\n" +
                    "  \"longitude\": " + updatedShipper.getCurrentLongitude() + ",\n" +
                    "  \"shipperId\": " + updatedShipper.getId() + "\n" +
                    "}");
        } catch (AppException e) {
            log.error("Error updating shipper location: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getErrorCode().getStatusCode().value())
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error updating shipper location", e);
            return ResponseEntity.status(500)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
}