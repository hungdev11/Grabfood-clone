package com.api.service;

import com.api.dto.request.ReviewDTO;
import com.api.dto.response.PageResponse;
import com.api.entity.CartDetail;
import com.api.entity.Food;
import com.api.entity.Order;
import com.api.entity.Review;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ReviewRepository;
import com.api.service.Imp.ReviewServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReviewServiceImp reviewService;

    private Order order;
    private Review review;
    private User user;
    private ReviewDTO.UserReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reviewService, "orderService", orderService);

        user = User.builder()
                .name("Test User")
                .build();
        user.setId(1L);

        Food food1 = Food.builder()
                .name("Pizza")
                .build();
        food1.setId(1L);

        Food food2 = Food.builder()
                .name("Burger")
                .build();
        food2.setId(2L);

        CartDetail cartDetail1 = CartDetail.builder()
                .food(food1)
                .quantity(2)
                .build();
        cartDetail1.setId(1L);

        CartDetail cartDetail2 = CartDetail.builder()
                .food(food2)
                .quantity(1)
                .build();
        cartDetail2.setId(2L);

        order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now().minusDays(1))
                .cartDetails(List.of(cartDetail1, cartDetail2))
                .build();
        order.setId(1L);

        review = Review.builder()
                .order(order)
                .orderString("Pizza, Burger")
                .rating(new BigDecimal("4.5"))
                .reviewMessage("Great food!")
                .createdAt(LocalDateTime.now())
                .build();
        review.setId(1L);

        reviewRequest = ReviewDTO.UserReviewRequest.builder()
                .orderId(1L)
                .rating(new BigDecimal("4.5"))
                .reviewMessage("Great food!")
                .build();
    }

    @Test
    void createReview_Success() {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(reviewRepository.existsByOrder(order)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // When
        long reviewId = reviewService.createReview(reviewRequest);

        // Then
        assertEquals(1L, reviewId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_ReviewAlreadyExists_ReturnsMinusOne() {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(reviewRepository.existsByOrder(order)).thenReturn(true);

        // When
        long reviewId = reviewService.createReview(reviewRequest);

        // Then
        assertEquals(-1, reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_OrderTooOld_ThrowsException() {
        // Given
        order.setOrderDate(LocalDateTime.now().minusDays(15)); // More than 10 days old
        when(orderService.getOrderById(1L)).thenReturn(order);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> reviewService.createReview(reviewRequest));
        assertEquals(ErrorCode.CANNOT_WRITE_REVIEW_DUE_DATE, exception.getErrorCode());
    }

    @Test
    void restaurantReplyReview_Success() {
        // Given
        ReviewDTO.RestaurantReplyRequest replyRequest = ReviewDTO.RestaurantReplyRequest.builder()
                .reviewId(1L)
                .replyMessage("Thank you for your feedback!")
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // When
        reviewService.restaurantReplyReview(replyRequest);

        // Then
        verify(reviewRepository).save(argThat(r ->
                "Thank you for your feedback!".equals(r.getReplyMessage()) &&
                        r.getReplyTime() != null));
    }

    @Test
    void restaurantReplyReview_ReviewNotFound_ThrowsException() {
        // Given
        ReviewDTO.RestaurantReplyRequest replyRequest = ReviewDTO.RestaurantReplyRequest.builder()
                .reviewId(1L)
                .replyMessage("Thank you for your feedback!")
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> reviewService.restaurantReplyReview(replyRequest));
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getReviewById_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When
        ReviewDTO.ReviewResponse response = reviewService.getReviewById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getReviewId());
        assertEquals("Test User", response.getCustomerName());
        assertEquals("Great food!", response.getReviewMessage());
        assertEquals(new BigDecimal("4.5"), response.getRating());
    }

    @Test
    void getReviewById_NotFound_ThrowsException() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> reviewService.getReviewById(1L));
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void calculateAvgRating_Success() {
        // Given
        long restaurantId = 1L;
        Review review1 = Review.builder()
                .rating(new BigDecimal("4.0"))
                .build();
        review1.setId(1L);
        Review review2 = Review.builder()
                .rating(new BigDecimal("5.0"))
                .build();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        List<Order> orders = Arrays.asList(order);

        when(orderService.listAllOrdersOfRestaurant(restaurantId)).thenReturn(orders);
        when(reviewRepository.findAllByOrderIn(orders)).thenReturn(reviews);

        // When
        BigDecimal avgRating = reviewService.calculateAvgRating(restaurantId);

        // Then
        assertEquals(new BigDecimal("4.5"), avgRating);
    }

    @Test
    void calculateAvgRating_NoReviews_ReturnsZero() {
        // Given
        long restaurantId = 1L;
        List<Order> orders = Arrays.asList(order);

        when(orderService.listAllOrdersOfRestaurant(restaurantId)).thenReturn(orders);
        when(reviewRepository.findAllByOrderIn(orders)).thenReturn(Arrays.asList());

        // When
        BigDecimal avgRating = reviewService.calculateAvgRating(restaurantId);

        // Then
        assertEquals(BigDecimal.ZERO, avgRating);
    }

    @Test
    void getReviewsByRestaurantId_Success() {
        // Given
        long restaurantId = 1L;
        Pageable pageRequest = PageRequest.of(0, 10);
        int ratingFilter = 6; // Show all ratings
        List<Order> orders = Arrays.asList(order);
        List<Review> reviews = Arrays.asList(review);

        when(orderService.listAllOrdersOfRestaurant(restaurantId)).thenReturn(orders);
        when(reviewRepository.findAllByOrderIn(orders)).thenReturn(reviews);

        // When
        PageResponse<List<ReviewDTO.ReviewResponse>> response =
                reviewService.getReviewsByRestaurantId(restaurantId, pageRequest, ratingFilter);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getItems().size());
        assertEquals("Great food!", response.getItems().get(0).getReviewMessage());
    }

    @Test
    void getReviewsByRestaurantId_WithRatingFilter_Success() {
        // Given
        long restaurantId = 1L;
        Pageable pageRequest = PageRequest.of(0, 10);
        int ratingFilter = 4; // Filter for 4-star reviews
        List<Order> orders = Arrays.asList(order);

        Review fourStarReview = Review.builder()
                .order(order)
                .rating(new BigDecimal("4"))
                .reviewMessage("Good food")
                .createdAt(LocalDateTime.now())
                .build();
        fourStarReview.setId(1L);
        Review fiveStarReview = Review.builder()
                .order(order)
                .rating(new BigDecimal("5"))
                .reviewMessage("Excellent food")
                .createdAt(LocalDateTime.now())
                .build();
        fiveStarReview.setId(2L);

        List<Review> reviews = Arrays.asList(fourStarReview, fiveStarReview);

        when(orderService.listAllOrdersOfRestaurant(restaurantId)).thenReturn(orders);
        when(reviewRepository.findAllByOrderIn(orders)).thenReturn(reviews);

        // When
        PageResponse<List<ReviewDTO.ReviewResponse>> response =
                reviewService.getReviewsByRestaurantId(restaurantId, pageRequest, ratingFilter);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotal()); // Only 4-star review should be returned
        assertEquals(1, response.getItems().size());
        assertEquals("Good food", response.getItems().get(0).getReviewMessage());
    }

    @Test
    void buildReviewResponse_Success() {
        // Given
        review.setReplyMessage("Thank you!");
        review.setReplyTime(LocalDateTime.now());

        // When
        ReviewDTO.ReviewResponse response = reviewService.buildReviewResponse(review);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getReviewId());
        assertEquals("Test User", response.getCustomerName());
        assertEquals(1L, response.getOrderId());
        assertEquals("Pizza, Burger", response.getOrderString());
        assertEquals("Great food!", response.getReviewMessage());
        assertEquals(new BigDecimal("4.5"), response.getRating());
        assertEquals("Thank you!", response.getReplyMessage());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getReplyAt());
    }

    @Test
    void getReviewsInOrders_Success() {
        // Given
        List<Order> orders = Arrays.asList(order);
        List<Review> expectedReviews = Arrays.asList(review);

        when(reviewRepository.findAllByOrderIn(orders)).thenReturn(expectedReviews);

        // When
        List<Review> actualReviews = reviewService.getReviewsInOrders(orders);

        // Then
        assertNotNull(actualReviews);
        assertEquals(1, actualReviews.size());
        assertEquals(review.getId(), actualReviews.get(0).getId());
        verify(reviewRepository).findAllByOrderIn(orders);
    }
}
