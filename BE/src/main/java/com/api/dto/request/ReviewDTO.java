package com.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;

public class ReviewDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class UserReviewRequest {
        private long orderId;
        private BigDecimal rating;
        private String reviewMessage;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class RestaurantReplyRequest {
        private long reviewId;
        private String replyMessage;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

}
