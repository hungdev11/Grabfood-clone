package com.app.grabfoodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private  Long id;
    private String detail;
    private String displayName;
    private boolean isDefault;
    private double lat;
    private double lon;
}
