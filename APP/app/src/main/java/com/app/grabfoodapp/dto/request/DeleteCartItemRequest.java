package com.app.grabfoodapp.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCartItemRequest {
    private Long foodId;
    private Long userId;
    private List<Long> additionalFoodIds;
}
