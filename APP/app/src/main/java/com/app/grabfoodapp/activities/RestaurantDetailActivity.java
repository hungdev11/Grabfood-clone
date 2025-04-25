package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.RestaurantDTO;

public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView restaurantName, restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_detail);

        restaurantName = findViewById(R.id.restaurantName);
        restaurantId = findViewById(R.id.restaurantId);

        // Nhận dữ liệu từ Intent
        RestaurantDTO.RestaurantResponse selectedRestaurant = (RestaurantDTO.RestaurantResponse) getIntent().getSerializableExtra("selectedRestaurant");

        // Hiển thị thông tin nhà hàng
        if (selectedRestaurant != null) {
            restaurantName.setText(selectedRestaurant.getName());
            restaurantId.setText(String.valueOf(selectedRestaurant.getId()));
        }
    }
}