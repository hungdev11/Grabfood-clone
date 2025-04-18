package com.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDistanceResponse {
    private String from;
    private String to;
    private Double distance;
    private Double duration;
}
