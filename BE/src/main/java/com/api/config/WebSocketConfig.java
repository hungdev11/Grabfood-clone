package com.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Cấu hình WebSocket cho real-time notifications
 * Hỗ trợ thông báo cho drivers, restaurants và users
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cấu hình message broker cho các topic khác nhau
        config.enableSimpleBroker("/topic"); // Nơi client subscribe để nhận message
        config.setApplicationDestinationPrefixes("/app"); // Prefix cho message từ client gửi lên server
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký WebSocket endpoint với CORS configuration
        registry.addEndpoint("/ws") // URL mà client sẽ connect tới
                .setAllowedOrigins("http://localhost:3000", "http://localhost:6969") // CORS cho frontend và mobile
                .withSockJS(); // Fallback SockJS cho browser không hỗ trợ WebSocket
    }
}

