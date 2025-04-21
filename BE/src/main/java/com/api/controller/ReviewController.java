package com.api.controller;

import com.api.dto.request.ReviewDTO;
import com.api.dto.response.ApiResponse;
import com.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<Long> createReview(@RequestBody ReviewDTO.UserReviewRequest request) {
        return ApiResponse.<Long>builder()
                .code(202)
                .message("Review created")
                .data(reviewService.createReview(request))
                .build();
    }

    @PostMapping("/reply")
    public ApiResponse<?> createReviewReply(@RequestBody ReviewDTO.RestaurantReplyRequest request) {
        reviewService.restaurantReplyReview(request);
        return ApiResponse.<Long>builder()
                .code(200)
                .message("Review updated")
                .build();
    }

    @GetMapping("{reviewId}")
    public ApiResponse<ReviewDTO.ReviewResponse> getReview(@PathVariable long reviewId) {
        return ApiResponse.<ReviewDTO.ReviewResponse>builder()
                .code(202)
                .message("Review created")
                .data(reviewService.getReview(reviewId))
                .build();
    }
}
