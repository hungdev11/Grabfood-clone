package com.api.controller;

import com.api.dto.request.AddUserRequest;
import com.api.dto.request.AuthRequest;
import com.api.entity.Account;
import com.api.repository.AccountRepository;
import com.api.service.AccountService;
import com.api.service.Imp.JwtService;
import com.api.service.Imp.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final UserInfoService userInfoService;
    @Autowired
    public UserController(UserInfoService userInfoService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          AccountService accountService) {
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to unsecured endpoint";
    }

    @PostMapping("/addNewAccount")
    public String addNewAccount(@RequestBody AddUserRequest request) {
        // Chỉ truyền mật khẩu gốc, việc encode sẽ do service xử lý
        return userInfoService.addAccount(request);
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
                log.debug("Authentication successful for user: {}", authRequest.getUsername());
                return jwtService.generateToken(authRequest.getUsername());
            }
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new UsernameNotFoundException("Invalid username or password");
        }

        throw new UsernameNotFoundException("Authentication failed");
    }
}