package com.api.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddVoucherDetailRequest {
    private int quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long voucher_id;
    private Long food_id;
}
