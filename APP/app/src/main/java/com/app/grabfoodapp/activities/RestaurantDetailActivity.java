package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.FoodCategoryAdapter;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.RestaurantDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {
    private ListView listViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_detail);

        listViewCategory = findViewById(R.id.res_listViewCategory);

        RestaurantDTO.RestaurantResponse selectedRestaurant =
                (RestaurantDTO.RestaurantResponse) getIntent().getSerializableExtra("selectedRestaurant");
        getFoodRestaurantHome(selectedRestaurant.getId());

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
        Call<ApiResponse<FoodDTO.GetFoodGroupResponse>> call = foodService.getFoodRestaurantHome(restaurantId);

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
                            foodGroup.getTypes(),    // Dùng luôn list, không cần .stream()
                            categoryFoodsMap
                    );
                    listViewCategory.setAdapter(adapter);
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
}
