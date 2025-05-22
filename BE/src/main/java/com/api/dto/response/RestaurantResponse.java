package com.api.dto.response;

import com.api.dto.request.AddressRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantResponse {
    private long id;
    private String name;
    private String image;
    private String phone;
    private String email;
    private String status;
    private LocalTime openingHour;
    private LocalTime closingHour;
    private String description;
    private String address;
    private BigDecimal rating;
    private String distance;
    private String timeDistance;
    private List<String> restaurantVouchersInfo;
}
