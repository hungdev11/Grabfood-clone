package com.api.controller;

import com.api.dto.request.ReviewDTO;
import com.api.dto.response.PageResponse;
import com.api.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createReview_ShouldReturnSuccessResponse() throws Exception {
        // Given
        ReviewDTO.UserReviewRequest request = new ReviewDTO.UserReviewRequest();
        Long expectedReviewId = 1L;
        when(reviewService.createReview(any(ReviewDTO.UserReviewRequest.class))).thenReturn(expectedReviewId);

        // When & Then
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(202))
                .andExpect(jsonPath("$.message").value("Review created"))
                .andExpect(jsonPath("$.data").value(expectedReviewId));

        verify(reviewService).createReview(any(ReviewDTO.UserReviewRequest.class));
    }

    @Test
    void createReviewReply_ShouldReturnSuccessResponse() throws Exception {
        // Given
        ReviewDTO.RestaurantReplyRequest request = new ReviewDTO.RestaurantReplyRequest();
        doNothing().when(reviewService).restaurantReplyReview(any(ReviewDTO.RestaurantReplyRequest.class));

        // When & Then
        mockMvc.perform(post("/reviews/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Review updated"));

        verify(reviewService).restaurantReplyReview(any(ReviewDTO.RestaurantReplyRequest.class));
    }

    @Test
    void getReview_ShouldReturnReviewResponse() throws Exception {
        // Given
        long reviewId = 1L;
        ReviewDTO.ReviewResponse expectedResponse = new ReviewDTO.ReviewResponse();
        when(reviewService.getReviewById(reviewId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(202))
                .andExpect(jsonPath("$.message").value("Review created"));

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    void getReviewOfRestaurant_WithDefaultParams_ShouldReturnPagedReviews() throws Exception {
        // Given
        long restaurantId = 1L;
        List<ReviewDTO.ReviewResponse> reviewsList = Arrays.asList(new ReviewDTO.ReviewResponse());
        PageResponse<List<ReviewDTO.ReviewResponse>> expectedResponse = PageResponse.<List<ReviewDTO.ReviewResponse>>builder()
                .items(reviewsList)
                .build();

        when(reviewService.getReviewsByRestaurantId(eq(restaurantId), any(Pageable.class), eq(6)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/reviews/restaurant/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get reviews of restaurant"));

        verify(reviewService).getReviewsByRestaurantId(eq(restaurantId), any(Pageable.class), eq(6));
    }

    @Test
    void getReviewOfRestaurant_WithCustomParams_ShouldReturnPagedReviews() throws Exception {
        // Given
        long restaurantId = 1L;
        int page = 1;
        int size = 10;
        int ratingFilter = 5;

        List<ReviewDTO.ReviewResponse> reviewsList = Arrays.asList(new ReviewDTO.ReviewResponse());
        PageResponse<List<ReviewDTO.ReviewResponse>> expectedResponse = PageResponse.<List<ReviewDTO.ReviewResponse>>builder()
                .items(reviewsList)
                .build();

        Pageable expectedPageable = PageRequest.of(page, size);
        when(reviewService.getReviewsByRestaurantId(restaurantId, expectedPageable, ratingFilter))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/reviews/restaurant/{restaurantId}", restaurantId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("ratingFilter", String.valueOf(ratingFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get reviews of restaurant"));

        verify(reviewService).getReviewsByRestaurantId(restaurantId, expectedPageable, ratingFilter);
    }

    @Test
    void getReviewOfRestaurant_WithZeroPage_ShouldReturnPagedReviews() throws Exception {
        // Given
        long restaurantId = 1L;
        int page = 0;
        int size = 5;
        int ratingFilter = 6;

        List<ReviewDTO.ReviewResponse> reviewsList = Arrays.asList(new ReviewDTO.ReviewResponse());
        PageResponse<List<ReviewDTO.ReviewResponse>> expectedResponse = PageResponse.<List<ReviewDTO.ReviewResponse>>builder()
                .items(reviewsList)
                .build();

        Pageable expectedPageable = PageRequest.of(page, size);
        when(reviewService.getReviewsByRestaurantId(restaurantId, expectedPageable, ratingFilter))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/reviews/restaurant/{restaurantId}", restaurantId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("ratingFilter", String.valueOf(ratingFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get reviews of restaurant"));

        verify(reviewService).getReviewsByRestaurantId(restaurantId, expectedPageable, ratingFilter);
    }

    @Test
    void getReviewOfRestaurant_WithDifferentRatingFilter_ShouldReturnFilteredReviews() throws Exception {
        // Given
        long restaurantId = 1L;
        int ratingFilter = 4;

        List<ReviewDTO.ReviewResponse> reviewsList = Arrays.asList(new ReviewDTO.ReviewResponse());
        PageResponse<List<ReviewDTO.ReviewResponse>> expectedResponse = PageResponse.<List<ReviewDTO.ReviewResponse>>builder()
                .items(reviewsList)
                .build();

        when(reviewService.getReviewsByRestaurantId(eq(restaurantId), any(Pageable.class), eq(ratingFilter)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/reviews/restaurant/{restaurantId}", restaurantId)
                        .param("ratingFilter", String.valueOf(ratingFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get reviews of restaurant"));

        verify(reviewService).getReviewsByRestaurantId(eq(restaurantId), any(Pageable.class), eq(ratingFilter));
    }
}
