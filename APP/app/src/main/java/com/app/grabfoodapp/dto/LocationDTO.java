package com.app.grabfoodapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private String province;
    private String district;
    private String ward;
    private String detail;
    private boolean isDefault;
    private double latitude;
    private double longitude;
}
