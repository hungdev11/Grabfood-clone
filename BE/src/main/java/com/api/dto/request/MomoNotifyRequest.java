package com.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoNotifyRequest {
    private String partnerCode;
    private Long orderId;
    private String requestId;
    private long amount;
    private String orderInfo;
    private String orderType;
    private long transId;
    private int resultCode;
    private String message;
    private String payType;
    private String signature;
}
