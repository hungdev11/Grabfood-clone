package com.api.service.Imp;

import com.api.dto.request.ReviewDTO;
import com.api.entity.Order;
import com.api.entity.Review;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ReviewRepository;
import com.api.service.OrderService;
import com.api.service.ReviewService;
import com.api.utils.TimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public long createReview(ReviewDTO.UserReviewRequest request) {
        // check user id from token

        log.info("Creating new review of order {}", request.getOrderId());
        Order order = orderService.getOrderById(request.getOrderId());

        // check order in time to be reviewed
        log.info("Check order time and now");
        if (order.getOrderDate().plusDays(10).isBefore(LocalDateTime.now())) {
            log.info("Can't create review because review time is past");
            throw new AppException(ErrorCode.CANNOT_WRITE_REVIEW_DUE_DATE);
        }

        if (reviewRepository.existsByOrder(order)) {
            log.info("Can't create review because review already exists");
            return -1;
        }

        Review review = Review.builder()
                .order(order)
                .createdAt(LocalDateTime.now())
                .rating(request.getRating())
                .build();

        if (request.getReviewMessage() != null && !request.getReviewMessage().trim().equals("")) {
            log.info("Updating review message to '{}'", request.getReviewMessage());
            review.setReviewMessage(request.getReviewMessage());
        }
        return reviewRepository.save(review).getId();
    }

    @Override
    @Transactional
    public void restaurantReplyReview(ReviewDTO.RestaurantReplyRequest request) {
        // check restaurant id from token

        log.info("Restaurant reply review");
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        review.setReplyMessage(request.getReplyMessage());
        review.setReplyTime(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    public ReviewDTO.ReviewResponse getReview(long reviewId) {
        log.info("Get review id {}", reviewId);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        return buildReviewResponse(review);
    }

    @Override
    public BigDecimal calculateAvgRating(long restaurantId) {
        log.info("calculate avg rating of restaurant {}", restaurantId);
        List<Review> reviews = reviewRepository.findAllByOrderIn(
                orderService.listAllOrdersOfRestaurant(restaurantId));

        BigDecimal sumRating = BigDecimal.ZERO;
        for (Review r : reviews) {
            sumRating = sumRating.add(r.getRating());
        }

        return reviews.isEmpty() ? BigDecimal.ZERO :
                sumRating.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP)
                        .setScale(1, RoundingMode.HALF_UP);
    }


    private ReviewDTO.ReviewResponse buildReviewResponse(Review review) {
        return ReviewDTO.ReviewResponse.builder()
                .reviewId(review.getId())
                .orderId(review.getOrder().getId())
                .reviewMessage(review.getReviewMessage())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt() == null ? null : TimeUtil.formatRelativeTime(review.getCreatedAt()) + " trước")
                .replyMessage(review.getReplyMessage() == null ? null : review.getReplyMessage())
                .replyAt(review.getReplyTime() == null ? null : TimeUtil.formatRelativeTime(review.getReplyTime()) + " trước")
                .build();
    }


}
