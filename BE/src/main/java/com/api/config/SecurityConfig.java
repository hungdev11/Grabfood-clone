package com.api.config;

import com.api.jwt.JwtAuthFilter;
import com.api.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // Constructor injection for required dependencies
    public SecurityConfig(@Lazy JwtAuthFilter jwtAuthFilter,
            UserDetailsService userDetailsService,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // http
    // .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅
    // Corrected method usage
    // .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers(new AntPathRequestMatcher("/restaurants")).permitAll() //
    // Allow API access
    // .anyRequest().authenticated()) // Secure other endpoints
    // .formLogin(form -> form.disable()) // Disable form login
    // .httpBasic(httpBasic -> httpBasic.disable()); // Disable basic auth
    //
    // return http.build();
    // }

    // ✅ CORS Configuration Source method
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:6969")); // Frontend và WebSocket
                                                                                             // URLs
        config.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Global CORS filter
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    /**
     * Main security configuration
     * Cấu hình phân quyền truy cập endpoint và JWT filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (không cần thiết cho JWT stateless)
                .csrf(csrf -> csrf.disable())

                // Disable form login và default login page
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // Configure endpoint authorization - Cấu hình phân quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Các endpoint công khai
                        .requestMatchers("/auth/welcome", "/auth/addNewAccount", "/auth/addNewAccount2",
                                "/auth/generateToken", "/auth/generateToken2")
                        .permitAll()
                        .requestMatchers("/login/oauth2/**", "/oauth2/**", "/oauth2/authorization/google").permitAll()

                        // WebSocket và Real-time endpoints
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/push-noti/**").permitAll()

                        // Public review, notification, report endpoints
                        .requestMatchers("/reviews/**").permitAll()
                        .requestMatchers("/notifications/**").permitAll()
                        .requestMatchers("/report/**").permitAll()
                        .requestMatchers("/reminders/**", "/reminders").permitAll()

                        // Admin endpoints - Chỉ ADMIN
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // 🚛 DRIVER/SHIPPER APIs - Cấu hình đặc biệt cho Driver App
                        .requestMatchers("/api/driver/login", "/api/driver/system/**").permitAll()
                        .requestMatchers("/api/driver/**").hasAuthority("ROLE_SHIPPER")

                        // Restaurant endpoints - Nhà hàng và Admin
                        .requestMatchers(HttpMethod.GET, "/restaurants/**", "/restaurants").permitAll()
                        .requestMatchers(HttpMethod.POST, "/restaurants").permitAll() // Đăng ký nhà hàng
                        .requestMatchers(HttpMethod.POST, "/restaurants/**").hasAnyAuthority("ROLE_RES", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/restaurants/**").hasAnyAuthority("ROLE_RES", "ROLE_ADMIN")

                        // Food và Food Type endpoints
                        .requestMatchers("/foods/**").permitAll()
                        .requestMatchers("/food-types/**").permitAll()

                        // Cart endpoints - Người dùng
                        .requestMatchers("/cart/test").permitAll()
                        .requestMatchers("/cart/**").hasAuthority("ROLE_USER")

                        // Order endpoints - Linh hoạt cho các role khác nhau
                        .requestMatchers("/order/**").permitAll() // Tạm thời permit all, có thể điều chỉnh sau

                        // Payment, Voucher, Location endpoints
                        .requestMatchers("/payments/**").permitAll()
                        .requestMatchers("/vouchers/**", "/voucherDetails/**").permitAll()
                        .requestMatchers("/location/**").permitAll()

                        // Auth protected endpoints
                        .requestMatchers("/auth/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_RES", "ROLE_SHIPPER")

                        // All other endpoints require authentication
                        .anyRequest().authenticated())

                // Stateless session (bắt buộc cho JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Exception handling để trả về JSON thay vì HTML
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write(
                                    "{\"code\":401,\"message\":\"Unauthorized - Token required\",\"data\":null}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(403);
                            response.getWriter().write(
                                    "{\"code\":403,\"message\":\"Access Denied - Insufficient permissions\",\"data\":null}");
                        }))

                // OAuth2 Login configuration
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService())));

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            return oauth2User;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean
     * Cần thiết cho programmatic authentication (ví dụ: trong /generateToken)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}