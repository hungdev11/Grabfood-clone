package com.api.oauth2;


import com.api.entity.Account;
import com.api.jwt.JwtService;
import com.api.jwt.UserInfoService;
import com.api.service.AccountService;
import com.api.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(UserService userService,JwtService jwtService, UserInfoService userInfoService, @Lazy AccountService accountService) {
        this.jwtService = jwtService;
        this.userInfoService = userInfoService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String redirectFE = "http://localhost:3000";
        // Check if user exists, otherwise create
        Account account = accountService.getAccountByUsername(email);
        Boolean isUserExist = userService.checkUserExistByEmail(email);
        if (account == null && isUserExist) {
            String errorRedirectUrl = redirectFE + "/login?error=email_already_used";
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
            return;
        }

        if (account == null ) {
            account = userInfoService.registerOAuth2User(email, name, "ROLE_USER");
        }

        // Generate JWT token
        String token = jwtService.generateToken(account.getUsername());

        // Redirect to frontend with token
        String redirectUrl = redirectFE + "/login?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}