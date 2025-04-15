package com.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MomoResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private long amount;
    private String payUrl;
    private String signature;
    private int resultCode;
    private String message;
}
