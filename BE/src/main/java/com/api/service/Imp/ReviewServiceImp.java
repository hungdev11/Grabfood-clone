package com.api.service.Imp;

import com.api.dto.request.ReviewDTO;
import com.api.dto.response.PageResponse;
import com.api.entity.CartDetail;
import com.api.entity.Order;
import com.api.entity.Review;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ReviewRepository;
import com.api.service.OrderService;
import com.api.service.RestaurantService;
import com.api.service.ReviewService;
import com.api.utils.PageUtils;
import com.api.utils.TimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    @Lazy // circle inject bean
    private OrderService orderService;

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
                .orderString(createOrderString(order))
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
    public ReviewDTO.ReviewResponse getReviewById(long reviewId) {
        log.info("Get review id {}", reviewId);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        return buildReviewResponse(review);
    }

    private String createOrderString (Order order) {
        log.info("Creating order string of {}", order.getId());
        var orderedFoodList = order.getCartDetails().stream()
                .map(cd -> cd.getFood())
                .collect(Collectors.toSet());
        StringJoiner sj = new StringJoiner(", ");
        orderedFoodList.forEach(food -> {sj.add(food.getName());});
        return sj.toString();
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

    @Override
    public PageResponse<List<ReviewDTO.ReviewResponse>> getReviewsByRestaurantId(long restaurantId, Pageable pageRequest, int ratingFilter) {
        log.info("Get reviews by restaurant id {}", restaurantId);
        List<Order> orders = orderService.listAllOrdersOfRestaurant(restaurantId);
        List<ReviewDTO.ReviewResponse> reviewResponses = reviewRepository.findAllByOrderIn(orders).stream()
                .filter(r -> ratingFilter == 6 || r.getRating().compareTo(BigDecimal.valueOf(ratingFilter)) == 0)
                .map(this::buildReviewResponse)
                .toList();
        var pageResponse = PageUtils.convertListToPage(reviewResponses, pageRequest);
        return PageResponse.<List<ReviewDTO.ReviewResponse>>builder()
                .total(pageResponse.getTotalElements())
                .items(pageResponse.getContent())
                .page(pageRequest.getPageNumber())
                .size(pageResponse.getSize())
                .build();
    }

    public ReviewDTO.ReviewResponse buildReviewResponse(Review review) {
        return ReviewDTO.ReviewResponse.builder()
                .reviewId(review.getId())
                .customerName(review.getOrder().getUser().getName())
                .orderId(review.getOrder().getId())
                .orderString(review.getOrderString())
                .reviewMessage(review.getReviewMessage())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt() == null ? null : TimeUtil.formatRelativeTime(review.getCreatedAt()) + " trước")
                .replyMessage(review.getReplyMessage() == null ? null : review.getReplyMessage())
                .replyAt(review.getReplyTime() == null ? null : TimeUtil.formatRelativeTime(review.getReplyTime()) + " trước")
                .build();
    }

    @Override
    public List<Review> getReviewsInOrders(List<Order> orders) {
        return reviewRepository.findAllByOrderIn(orders);
    }
}
