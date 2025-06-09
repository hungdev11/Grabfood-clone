package com.app.grabfoodapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class RestaurantDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class RestaurantResponse implements Serializable {
        private long id;
        private String name;
        private String image;
        private String phone;
        private String openingHour;
        private String closingHour;
        private String description;
        private String address;
        private BigDecimal rating;
        private String distance;
        private String timeDistance;
        private List<String> restaurantVouchersInfo;
        private BigDecimal shippingFee;
    }
}
