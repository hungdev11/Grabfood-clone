package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.FoodCategoryAdapter;
import com.app.grabfoodapp.adapter.FoodTypeStringAdapter;
import com.app.grabfoodapp.adapter.ReviewHoriAdapter;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.apiservice.review.ReviewService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.RestaurantDTO;
import com.app.grabfoodapp.dto.ReviewDTO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResInfoDetail extends AppCompatActivity {
    private ImageView resImg;
    private TextView resName;
    private TextView resRating;
    private RecyclerView someReviews;
    private List<ReviewDTO.ReviewResponse> reviewResponseList = new ArrayList<>();
    private TextView resOpeningHours;
    private TextView resAddress;
    private TextView resDistance;
    private RestaurantDTO.RestaurantResponse selectedResInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_res_info_detail);
        selectedResInfo = (RestaurantDTO.RestaurantResponse) getIntent().getSerializableExtra("selectedResInfo");
        init();
        getSomeReviews(selectedResInfo.getId());
    }

    private void init() {
        resImg = findViewById(R.id.res_detail_image);
        resName = findViewById(R.id.res_detail_name);
        resRating = findViewById(R.id.res_detail_rating);
        resOpeningHours = findViewById(R.id.res_detail_open_hours);
        resAddress = findViewById(R.id.res_detail_address);
        resDistance = findViewById(R.id.res_detail_distance);

        Glide.with(this).load(selectedResInfo.getImage()).into(resImg);
        resName.setText(selectedResInfo.getName());
        resRating.setText(selectedResInfo.getRating().toString());
        resOpeningHours.setText(resOpeningHours.getText() + " : " + selectedResInfo.getOpeningHour() + " - " + selectedResInfo.getClosingHour());
        resAddress.setText(selectedResInfo.getAddress());
        resDistance.setText(selectedResInfo.getTimeDistance() + " * " + selectedResInfo.getDistance());

        someReviews = findViewById(R.id.res_detail_quick_reviews);
    }

    private void getSomeReviews(long restaurantId) {
        ReviewService reviewService = ApiClient.getClient().create(ReviewService.class);
        Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call
                = reviewService.getReviewOfRestaurant(restaurantId, 0, 10, 6);

        call.enqueue(new Callback<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call,
                                   Response<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> response) {
                Log.d("aaaaaa", String.valueOf(response.isSuccessful()));
                if (response.isSuccessful() && response.body() != null) {
                    reviewResponseList.clear();
                    reviewResponseList.addAll(response.body().getData().getItems());
                    Log.d("NANDAIDIJ","X+" + reviewResponseList.size());
                    ReviewHoriAdapter adapter = new ReviewHoriAdapter(reviewResponseList);
                    someReviews.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ResInfoDetail.this, LinearLayoutManager.HORIZONTAL, false);
                    someReviews.setLayoutManager(layoutManager);
                }else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }
    public void backToRestaurant(View view) {
        finish();
    }
    public void lookReviews (View view) {
        long restaurantId = selectedResInfo.getId();
        Intent intent = new Intent(this, ReviewsPage.class);
        intent.putExtra("restaurantId", restaurantId);
        startActivity(intent);
    }
}