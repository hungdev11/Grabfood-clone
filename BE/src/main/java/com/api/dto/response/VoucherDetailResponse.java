package com.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDetailResponse {
    private int quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long voucher_id;
    private List<Long> food_ids;
}
