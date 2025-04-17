package com.api.dto.request;

import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {

    private String code;

    @NonNull
    private String description;

    private int quantity;

    private BigDecimal minRequire;

    private VoucherType type;

    private BigDecimal value;

    private VoucherApplyType applyType;

    private VoucherStatus status;

    private long restaurant_id;
}
