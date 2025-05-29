package com.api.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipperLocationRequest {
    private Double latitude;
    private Double longitude;
}