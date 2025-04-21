package com.api.service;

import com.api.dto.request.ReviewDTO;

import java.math.BigDecimal;

public interface ReviewService {
    long createReview(ReviewDTO.UserReviewRequest request);
    void restaurantReplyReview(ReviewDTO.RestaurantReplyRequest request);
    ReviewDTO.ReviewResponse getReview(long reviewId);
    BigDecimal calculateAvgRating(long restaurantId);
}
