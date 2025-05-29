package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.ReviewAdapter;
import com.app.grabfoodapp.adapter.ReviewHoriAdapter;
import com.app.grabfoodapp.apiservice.review.ReviewService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.ReviewDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsPage extends AppCompatActivity {

    private Long restaurantId;
    private ReviewAdapter adapter;
    private RecyclerView recyclerView;

    private int currentPage = 0;
    private int pageSize = 5;
    private int totalPages = 1; // sẽ cập nhật từ response
    private int ratingFilter = 6; // 6 nghĩa là "All"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reviews_page);

        restaurantId =  (Long) getIntent().getSerializableExtra("restaurantId");
        if (restaurantId == null) {
            Log.e("NULL", "Res id in review page is null");
            finish();
        }

        Toolbar toolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setTitle(selectedRestaurant.getName());
        }

        Spinner spinner = findViewById(R.id.spinner_filter_rating);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"All", "5★", "4★", "3★", "2★", "1★"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ratingFilter = (position == 0) ? 6 : 6 - position; // All -> 6, 5★ -> 5, ...
                currentPage = 0;
                getReviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button btnPrev = findViewById(R.id.btn_prev);
        Button btnNext = findViewById(R.id.btn_next);
        TextView tvPage = findViewById(R.id.tv_page_number);

        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                getReviews();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                getReviews();
            }
        });



        recyclerView = findViewById(R.id.recycler_reviews);
        adapter = new ReviewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getReviews();
    }

    private void getReviews() {
        ReviewService reviewService = ApiClient.getClient().create(ReviewService.class);
        Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call =
                reviewService.getReviewOfRestaurant(restaurantId, currentPage, pageSize, ratingFilter);

        call.enqueue(new Callback<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call,
                                   Response<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<List<ReviewDTO.ReviewResponse>> pageResponse = response.body().getData();
                    if (pageResponse != null) {
                        List<ReviewDTO.ReviewResponse> items = pageResponse.getItems();
                        adapter.setReviewList(items != null ? items : new ArrayList<>());

                        long total = pageResponse.getTotal();
                        long size = pageResponse.getSize();
                        totalPages = (int) ((total + size - 1) / size); // làm tròn lên

                        TextView tvPage = findViewById(R.id.tv_page_number);
                        tvPage.setText("Page " + (currentPage + 1) + "/" + totalPages);

                        Button btnPrev = findViewById(R.id.btn_prev);
                        Button btnNext = findViewById(R.id.btn_next);
                        btnPrev.setEnabled(currentPage > 0);
                        btnNext.setEnabled(currentPage < totalPages - 1);
                    }
                } else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<ReviewDTO.ReviewResponse>>>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}