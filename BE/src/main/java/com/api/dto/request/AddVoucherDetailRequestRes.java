package com.api.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddVoucherDetailRequestRes {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long voucher_id;
    private List<Long> foodIds;
}
