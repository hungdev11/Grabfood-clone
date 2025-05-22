package com.api.dto.request;

import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateRestaurantRequest {
    private String name;
    private String image;
    private String phone;
    private LocalTime openingHour;
    private LocalTime closingHour;
    private String description;
    private AddressRequest address;
}