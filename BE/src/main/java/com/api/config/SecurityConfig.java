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
                          UserDetailsService userDetailsService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Corrected method usage
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(new AntPathRequestMatcher("/restaurants")).permitAll() // Allow API access
//                        .anyRequest().authenticated()) // Secure other endpoints
//                .formLogin(form -> form.disable()) // Disable form login
//                .httpBasic(httpBasic -> httpBasic.disable()); // Disable basic auth
//
//        return http.build();
//    }

    // ✅ Corrected: Add the missing CORS Configuration Source method
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend URL
        config.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Optional: Global CORS filter (if needed)
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    /*
     * Main security configuration
     * Defines endpoint access rules and JWT filter setup
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT)
                .csrf(csrf -> csrf.disable())

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                                // Public endpoints
                                .requestMatchers("/auth/welcome", "/auth/addNewAccount", "/auth/generateToken").permitAll()
                                //
                                .requestMatchers("/reviews/**").permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .requestMatchers("/push-noti/**").permitAll()
                                .requestMatchers("/cart/test").permitAll()

                                //
                                // Role-based endpoints
                                .requestMatchers("/auth/user/**").hasAuthority("ROLE_USER")
                                .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/cart/**").permitAll()
                                .requestMatchers("/order/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/restaurants/**").hasAnyAuthority("ROLE_RES", "ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/restaurants/**", "/restaurants").permitAll()
                                .requestMatchers("/cart/**").hasAuthority("ROLE_USER")
                                .requestMatchers("/order/**").permitAll()//hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                                //.requestMatchers({HttpMethod.POST, HttpMethod.PUT}, "/restaurants/**").hasAnyAuthority("ROLE_RES", "ROLE_ADMIN")
                                .requestMatchers(/*HttpMethod.GET,*/ "/restaurants/**", "/restaurants").permitAll()
                                .requestMatchers("/login/oauth2/**", "/oauth2/**","/oauth2/authorization/google").permitAll()
                                .requestMatchers("/foods/**").permitAll()
                                .requestMatchers("/food-types").permitAll()
                                .requestMatchers("/vouchers/**", "/voucherDetails/**").permitAll()
                                .requestMatchers("/payments/**").permitAll()
                                .requestMatchers("/location/**").permitAll()
//                        .requestMatchers("/cart/**", "/order/**", "/restaurants/**").permitAll()
//                        .requestMatchers("/foods/**","food-types").permitAll()
//                        .requestMatchers("/vouchers/**", "/voucherDetails/**").permitAll()
                                // All other endpoints require authentication
                                .anyRequest().authenticated()
                )

                // Stateless session (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
        );

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

    /*
     * Authentication manager bean
     * Required for programmatic authentication (e.g., in /generateToken)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
