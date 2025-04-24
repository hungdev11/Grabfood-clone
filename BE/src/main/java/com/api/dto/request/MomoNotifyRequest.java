package com.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoNotifyRequest {
    private Long orderId;
    private String requestId;
    private long amount;
    private int resultCode;
}
