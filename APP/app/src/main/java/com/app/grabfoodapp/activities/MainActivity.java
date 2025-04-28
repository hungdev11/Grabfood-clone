package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.grabfoodapp.R;
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

    private ImageButton btnCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewRestaurants);
        btnCart = findViewById(R.id.btn_cart);
        // Xử lý sự kiện nhấn nút giỏ hàng
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
        adapter = new RestaurantAdapter(this, restaurantList);
        listView.setAdapter(adapter);
        test();
        restaurantItemClicked(); // Gọi hàm xử lý sự kiện click
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

    private void restaurantItemClicked() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Lấy thông tin restaurant khi item được click
                RestaurantDTO.RestaurantResponse selectedRestaurant = restaurantList.get(position);

                // Tạo Intent để mở Activity mới và truyền dữ liệu
                Intent intent = new Intent(MainActivity.this, RestaurantDetailActivity.class);
                // Truyền RestaurantResponse qua Intent
                intent.putExtra("selectedRestaurant", selectedRestaurant);
                startActivity(intent);
            }
        });
    }
}
