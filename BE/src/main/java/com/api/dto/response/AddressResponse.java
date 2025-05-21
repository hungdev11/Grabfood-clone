package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
