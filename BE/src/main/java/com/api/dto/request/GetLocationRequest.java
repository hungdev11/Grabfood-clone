package com.api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetLocationRequest {
    private double lat;
    private double lon;
}
