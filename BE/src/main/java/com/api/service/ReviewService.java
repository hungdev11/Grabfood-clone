package com.api.service;

import com.api.dto.request.ReviewDTO;
import com.api.dto.response.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ReviewService {
    long createReview(ReviewDTO.UserReviewRequest request);
    void restaurantReplyReview(ReviewDTO.RestaurantReplyRequest request);
    ReviewDTO.ReviewResponse getReviewById(long reviewId);
    BigDecimal calculateAvgRating(long restaurantId);
    PageResponse<List<ReviewDTO.ReviewResponse>> getReviewsByRestaurantId(long restaurantId, Pageable pageRequest, int ratingFilter);
}
