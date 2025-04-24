package com.app.grabfoodapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.grabfoodapp.adapter.RestaurantAdapter;
import com.app.grabfoodapp.apiservice.restaurant.RestaurantService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.RestaurantDTO;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private RestaurantAdapter adapter;
    private List<RestaurantDTO.RestaurantResponse> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewRestaurants);
        adapter = new RestaurantAdapter(this, restaurantList);
        listView.setAdapter(adapter);
        test();
    }

    private void test() {
        RestaurantService restaurantService = ApiClient.getClient().create(RestaurantService.class);

        Call<ApiResponse<PageResponse<List<RestaurantDTO.RestaurantResponse>>>> call =
                restaurantService.getRestaurants("name", 0, 20);

        call.enqueue(new retrofit2.Callback<ApiResponse<PageResponse<List<RestaurantDTO.RestaurantResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<RestaurantDTO.RestaurantResponse>>>> call,
                                   retrofit2.Response<ApiResponse<PageResponse<List<RestaurantDTO.RestaurantResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    restaurantList.clear();
                    restaurantList.addAll(response.body().getData().getItems());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<RestaurantDTO.RestaurantResponse>>>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });

    }
}