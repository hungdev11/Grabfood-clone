package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class GrabfoodAPI {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "12345678";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println("Mật khẩu gốc: " + rawPassword);
        System.out.println("Mật khẩu đã mã hóa: " + hashedPassword);
        SpringApplication.run(GrabfoodAPI.class, args);
    }
}