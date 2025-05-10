package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.FoodCategoryAdapter;
import com.app.grabfoodapp.adapter.FoodTypeStringAdapter;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.RestaurantDTO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {
    private ImageView ivResImg;
    private TextView tvResName;
    private TextView tvResRating;
    private TextView tvTimeDistance;
    private ListView listViewCategory;
    private RecyclerView resRecyclerViewFoodTypes;

    private RestaurantDTO.RestaurantResponse selectedRestaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_detail);

        listViewCategory = findViewById(R.id.res_listViewCategory);
        resRecyclerViewFoodTypes = findViewById(R.id.res_recyclerViewFoodTypes);
        selectedRestaurant = (RestaurantDTO.RestaurantResponse) getIntent().getSerializableExtra("selectedRestaurant");
        getFoodRestaurantHome(selectedRestaurant.getId());

        setRestaurantInfo(selectedRestaurant);

        Toolbar toolbar = findViewById(R.id.res_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(selectedRestaurant.getName());
        }
    }

    private void getFoodRestaurantHome(long restaurantId) {
        FoodService foodService = ApiClient.getClient().create(FoodService.class);
        Call<ApiResponse<FoodDTO.GetFoodGroupResponse>> call = foodService.getFoodRestaurantHome(restaurantId, true);

        call.enqueue(new Callback<ApiResponse<FoodDTO.GetFoodGroupResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodDTO.GetFoodGroupResponse>> call,
                                   Response<ApiResponse<FoodDTO.GetFoodGroupResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FoodDTO.GetFoodGroupResponse foodGroup = response.body().getData();

                    Log.d("API", "Số loại món: " + foodGroup.getTypes().size());
                    Log.d("API", "Số món ăn: " + foodGroup.getFoods().size());

                    Map<String, List<FoodDTO.GetFoodResponse>> categoryFoodsMap = new HashMap<>();

                    for (String type : foodGroup.getTypes()) {
                        List<FoodDTO.GetFoodResponse> foodsOfType = new ArrayList<>();
                        for (FoodDTO.GetFoodResponse food : foodGroup.getFoods()) {
                            if (type.equals(food.getType())) {
                                foodsOfType.add(food);
                            }
                        }
                        categoryFoodsMap.put(type, foodsOfType);
                    }

                    FoodCategoryAdapter adapter = new FoodCategoryAdapter(
                            RestaurantDetailActivity.this,
                            foodGroup.getTypes(),
                            categoryFoodsMap,
                            restaurantId
                    );
                    listViewCategory.setAdapter(adapter);
                    FoodTypeStringAdapter foodTypeAdapter = new FoodTypeStringAdapter(RestaurantDetailActivity.this, foodGroup.getTypes());
                    resRecyclerViewFoodTypes.setAdapter(foodTypeAdapter);

// Set layout ngang cho RecyclerView
                    LinearLayoutManager layoutManager = new LinearLayoutManager(RestaurantDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    resRecyclerViewFoodTypes.setLayoutManager(layoutManager);

// Optional: xử lý click vào 1 loại món ăn
                    foodTypeAdapter.setOnItemClickListener(selectedType -> {
                        // Tìm vị trí trong ListView theo loại món
                        int position = findFirstFoodPositionByType(foodGroup, selectedType);
                        if (position != -1) {
                            listViewCategory.smoothScrollToPosition(position);
                        }
                    });

                } else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodDTO.GetFoodGroupResponse>> call, Throwable t) {
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
    private int findFirstFoodPositionByType(FoodDTO.GetFoodGroupResponse foodGroup, String type) {
        // Lấy vị trí của loại trong foodGroup.getTypes()
        for (int i = 0; i < foodGroup.getTypes().size(); i++) {
            if (type.equals(foodGroup.getTypes().get(i))) {
                return i;
            }
        }
        return -1; // Không tìm thấy
    }

    private void setRestaurantInfo(RestaurantDTO.RestaurantResponse resInfo) {
        ivResImg = findViewById(R.id.res_image);
        tvResName = findViewById(R.id.res_name);
        tvResRating = findViewById(R.id.res_rating);
        tvTimeDistance = findViewById(R.id.res_time_distance);

        Glide.with(this).load(resInfo.getImage()).into(ivResImg);
        tvResName.setText(resInfo.getName());
        tvResRating.setText(resInfo.getRating().toString());
        tvTimeDistance.setText("Cách " + resInfo.getTimeDistance());
    }

    public void getMoreResDetails(View view) {
        Log.e("INFO", "Item clicked for more res detail: " + selectedRestaurant.getName());
        Intent intent = new Intent(this, ResInfoDetail.class);
        intent.putExtra("selectedResInfo", selectedRestaurant);
        this.startActivity(intent);
    }
}
