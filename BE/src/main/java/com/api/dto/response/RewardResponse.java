package com.api.dto.response;

import com.api.utils.RewardType;
import com.api.utils.ClaimStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {

    private Long id;
    private String title;
    private String description;
    private RewardType type;
    private BigDecimal rewardValue;
    private Integer gemsValue;

    // Requirements
    private Integer requiredOrders;
    private Float requiredDistance;
    private Float requiredRating;

    // Validity
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Progress info for this shipper
    private ClaimStatus claimStatus;
    private Float progressValue;
    private Float completionPercentage;
    private Boolean isClaimable;

    // UI
    private String iconUrl;
}