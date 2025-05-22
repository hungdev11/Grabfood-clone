package com.api.dto.request;

import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddRestaurantRequest {
    private String name;
    private String image;
    private String phone;
    private String email;
    private LocalTime openingHour;
    private LocalTime closingHour;
    private String description;
    private AddressRequest address;
//    private String username;
//    private String password;
}
