package com.app.grabfoodapp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ReviewResponse {
        private long reviewId;
        private long orderId;
        private String customerName;
        private String orderString;
        private String reviewMessage;
        private String createdAt;
        private BigDecimal rating;
        private String replyMessage;
        private String replyAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class CreateReviewRequest {
        private long orderId;
        private BigDecimal rating;
        private String reviewMessage;
    }
}
