package com.api.controller;

import com.api.dto.request.ShipperAuthRequest;
import com.api.dto.response.ShipperAuthResponse;
import com.api.dto.response.ShipperProfileResponse;
import com.api.entity.Account;
import com.api.entity.Shipper;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.jwt.JwtService;
import com.api.service.AccountService;
import com.api.service.ShipperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/shipper")
@RequiredArgsConstructor
@Slf4j
public class ShipperAuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final ShipperService shipperService;

    @PostMapping("/login")
    public ResponseEntity<ShipperAuthResponse> login(@RequestBody ShipperAuthRequest request) {
        log.debug("Shipper authentication attempt for phone: {}", request.getPhone());

        try {
            // Authenticate using phone as username
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhone(),
                            request.getPassword()));

            if (authentication.isAuthenticated()) {
                // Get account and verify it's a shipper
                Account account = accountService.getAccountByUsername(request.getPhone());

                // Check if account has SHIPPER role
                if (!"ROLE_SHIPPER".equals(account.getRole().getRoleName())) {
                    return ResponseEntity.status(403).body(
                            ShipperAuthResponse.builder()
                                    .message("Access denied. Not a shipper account.")
                                    .phone(request.getPhone())
                                    .build());
                }

                // Get shipper information
                Shipper shipper = shipperService.getShipperByAccountId(account.getId());

                // Generate token
                String token = jwtService.generateToken(request.getPhone());

                ShipperAuthResponse response = ShipperAuthResponse.builder()
                        .token(token)
                        .message("Authentication successful")
                        .phone(request.getPhone())
                        .shipperId(shipper.getId())
                        .name(shipper.getName())
                        .status(shipper.getStatus().toString())
                        .isOnline(shipper.getIsOnline())
                        .build();

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(
                        ShipperAuthResponse.builder()
                                .message("Authentication failed")
                                .phone(request.getPhone())
                                .build());
            }
        } catch (Exception e) {
            log.error("Authentication failed for shipper: {}", request.getPhone(), e);
            return ResponseEntity.status(401).body(
                    ShipperAuthResponse.builder()
                            .message("Invalid phone number or password")
                            .phone(request.getPhone())
                            .build());
        }
    }

    @PostMapping("/test-password")
    public ResponseEntity<String> testPassword(@RequestParam String password, @RequestParam String hash) {
        boolean matches = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(password,
                hash);
        return ResponseEntity.ok("Password '" + password + "' matches hash: " + matches);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Clear security context
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("{\"message\": \"Logout successful\"}");
    }

    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<ShipperProfileResponse> getProfile() {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting profile for authenticated user: {}", phone);

            // Get account and shipper information
            Account account = accountService.getAccountByUsername(phone);
            if (account == null) {
                log.error("Account not found for username: {}", phone);
                return ResponseEntity.status(404).build();
            }

            Shipper shipper = shipperService.getShipperByAccountId(account.getId());

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
}