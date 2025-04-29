package com.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String province;
    private String district;
    private String ward;
    private String detail;
    private boolean isDefault;
    private double latitude;
    private double longitude;
}
