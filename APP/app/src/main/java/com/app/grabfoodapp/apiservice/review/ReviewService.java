package com.app.grabfoodapp.apiservice.review;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.ReviewDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewService {
    @GET("reviews/restaurant/{restaurantId}")
    Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> getReviewOfRestaurant(
            @Path("restaurantId") long restaurantId,
            @Query("page") int page,
            @Query("size") int size,
            @Query("ratingFilter") int ratingFilter);

    @POST("reviews")
    Call<ApiResponse<Long>> sendReview(@Body ReviewDTO.CreateReviewRequest request);
}
