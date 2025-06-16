package com.api.controller;

import com.api.dto.request.*;
import com.api.dto.response.LoginResponse;
import com.api.dto.response.UserResponse;
import com.api.entity.Account;
import com.api.entity.Restaurant;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.service.AccountService;
import com.api.jwt.JwtService;
import com.api.jwt.UserInfoService;
import com.api.service.UserService;
import com.api.utils.RestaurantStatus;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;


import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final UserInfoService userInfoService;
    private final UserService userService;
    @Autowired
    public UserController(UserService userService,
                          UserInfoService userInfoService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          AccountService accountService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserResponse> getCurrentUserInfo() {
        // Get authenticated username from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Get account and user information
        Account account = accountService.getAccountByUsername(username);
        User user = userService.getUserByAccountId(account.getId());

        // Create response DTO
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .username(account.getUsername())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        // Get authenticated username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Verify passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match");
        }

        // Change password
        try {
            accountService.changePassword(username, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            accountService.generatePasswordResetToken(request.getEmail());
            return ResponseEntity.ok("Password reset email sent");
        } catch (AppException e) {
            log.error("Error in forgot password request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            boolean valid = accountService.validatePasswordResetToken(token);
            return ResponseEntity.ok().build();
        } catch (AppException e) {
            log.error("Invalid reset token", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match");
        }

        try {
            accountService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successful");
        } catch (AppException e) {
            log.error("Error resetting password", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addNewAccount")
    public String addNewAccount(@RequestBody AddUserRequest request) {
        // Chỉ truyền mật khẩu gốc, việc encode sẽ do service xử lý
        return userInfoService.addAccount(request);
    }

    @PostMapping("/addNewAccount2")
    public ResponseEntity<LoginResponse> addNewAccount2(@RequestBody AddUserRequest request) {
        try {
            // Create the account using the existing service
            userInfoService.addAccount(request);

            // Generate token for the new user (using phone as username)
            String generatedToken = jwtService.generateToken(request.getPhone());

            // Create and return login response
            LoginResponse response = LoginResponse.builder()
                    .token(generatedToken)
                    .message("Account created successfully")
                    .username(request.getPhone())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AppException e) {
            // Handle application exceptions (like duplicate username)
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(e.getMessage())
                            .username(request.getPhone())
                            .build()
            );
        } catch (Exception e) {
            // Handle unexpected exceptions
            log.error("Error creating account", e);
            return ResponseEntity.status(500).body(
                    LoginResponse.builder()
                            .message("Error creating account: " + e.getMessage())
                            .username(request.getPhone())
                            .build()
            );
        }
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        // Debug logging (nên dùng logger thay vì System.out)
        log.debug("Authentication attempt for user: {}", authRequest.getUsername());

        // Xác thực thông qua AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                Account account = accountService.getAccountByUsername(authRequest.getUsername());
                Restaurant restaurant = account.getRestaurant();
                if (restaurant != null && restaurant.getStatus() != RestaurantStatus.ACTIVE) {
                    log.warn("Login attempt for inactive restaurant by user: {}", authRequest.getUsername());
                    throw new UsernameNotFoundException("Restaurant is not active");
                }
                log.debug("Authentication successful for user: {}", authRequest.getUsername());
                return jwtService.generateToken(authRequest.getUsername());
            }
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new UsernameNotFoundException("Invalid username or password");
        }

        throw new UsernameNotFoundException("Authentication failed");
    }
    @PostMapping("/generateToken2")
    public ResponseEntity<LoginResponse> authenticateAndGetToken2(@RequestBody AuthRequest authRequest) {
        log.debug("Authentication attempt for user: {}", authRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                log.debug("Authentication successful for user: {}", authRequest.getUsername());
                String generatedToken = jwtService.generateToken(authRequest.getUsername());

                LoginResponse response = LoginResponse.builder()
                        .token(generatedToken)
                        .message("Authentication successful")
                        .username(authRequest.getUsername())
                        .build();

                return ResponseEntity.ok(response);
            } else {
                log.error("Authentication failed for user: {}", authRequest.getUsername());
                return ResponseEntity.status(401).body(
                        LoginResponse.builder()
                                .message("Authentication failed")
                                .username(authRequest.getUsername())
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            return ResponseEntity.status(401).body(
                    LoginResponse.builder()
                            .message("Invalid username or password")
                            .username(authRequest.getUsername())
                            .build()
            );
        }
    }
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleTokenRequest request) {
        try {
            // Verify the ID token with Google
            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList("474635444717-2b144chovgf1qes8pvdj86qcquqdassn.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", 401, "message", "Invalid Google token", "data", null));
            }

            // Extract user info from the token
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Check if user exists
            Account account = accountService.getAccountByUsername(email);
            Boolean isUserExist = userService.checkUserExistByEmail(email);

            if (account == null && isUserExist) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("code", 409, "message", "Email already used with different auth method", "data", null));
            }

            // Create account if it doesn't exist
            if (account == null) {
                account = userInfoService.registerOAuth2User(email, name, "ROLE_USER");
            }

            // Generate JWT token
            String token = jwtService.generateToken(account.getUsername());

            // Return token response
            Map<String, Object> responseData = Map.of("token", token, "username", email);
            return ResponseEntity.ok(Map.of("code", 200, "message", "Login successful", "data", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", 500, "message", "Authentication failed: " + e.getMessage(), "data", null));
        }
    }

}