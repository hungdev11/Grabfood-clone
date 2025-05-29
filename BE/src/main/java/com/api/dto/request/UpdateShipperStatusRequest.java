package com.api.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipperStatusRequest {
    private Boolean isOnline;
}