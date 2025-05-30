package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class GrabfoodAPI {
    public static void main(String[] args) {
        // Tạo BCrypt encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Password cần mã hóa
        String rawPassword = "12345678";

        // Mã hóa password
        String encodedPassword = encoder.encode(rawPassword);

        // In ra kết quả
        System.out.println("========================================");
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("BCrypt Hash: " + encodedPassword);
        System.out.println("========================================");

        // Kiểm tra xem hash có match với password không
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password matches hash: " + matches);
        System.out.println("========================================");

        // Tạo thêm vài hash khác nhau cho cùng 1 password (để test)
        System.out.println("Additional hashes for same password:");
        for (int i = 1; i <= 3; i++) {
            String hash = encoder.encode(rawPassword);
            System.out.println("Hash " + i + ": " + hash);
        }
        System.out.println("========================================");

        // Khởi động Spring Boot application
        SpringApplication.run(GrabfoodAPI.class, args);
    }
}