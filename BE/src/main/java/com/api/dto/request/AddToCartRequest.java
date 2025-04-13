package com.api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddToCartRequest {
    private Long foodId;
    private int quantity;
    private List<Long> additionalItems;
    private String note;
}
