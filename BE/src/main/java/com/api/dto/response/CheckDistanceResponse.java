package com.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckDistanceResponse {
    private boolean check;
    private Double distance;
    private Double duration;
}
