package com.api.dto.response;

import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private long id;

    private String code;

    private String description;

    private BigDecimal minRequire;

    private VoucherType type;

    private BigDecimal value;

    private VoucherApplyType applyType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private VoucherStatus status;

    private String restaurant_name;

    private boolean isActive;

    private List<Long> foodIds;
}
