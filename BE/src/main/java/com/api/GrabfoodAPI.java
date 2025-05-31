package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GrabfoodAPI {
    public static void main(String[] args) {
        SpringApplication.run(GrabfoodAPI.class, args);
    }
}
