package com.app.grabfoodapp.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse implements Serializable {
    private long id;

    private String code;

    private String description;

    private BigDecimal minRequire;

    private String type;

    private BigDecimal value;

    private String applyType;

    private String endTime;

    private String restaurant_name;
}
