package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.AdditionalFoodAdapter;
import com.app.grabfoodapp.apiservice.cart.CartService;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.CartDTO;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.utils.Util;
import com.bumptech.glide.Glide;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopUpFood extends AppCompatActivity {
    private ImageView foodImage;
    private TextView foodName;
    private TextView foodPrice;
    private TextView foodDes;
    private EditText note;
    private TextView tvQuantity;
    private Button increaseButton;
    private Button decreaseButton;
    private Button addToCartButton;
    private ListView lvAdditionalFoods;
    private List<FoodDTO.GetFoodResponse> addiList = new ArrayList<>();
    private FoodDTO.GetFoodResponse selectedFood;

    private AdditionalFoodAdapter adapter;

    private String formatTotalPrice(BigDecimal discountPrice, int quantity) {
        BigDecimal newTotalPrice = discountPrice.multiply(BigDecimal.valueOf(quantity));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(newTotalPrice);
    }
    public void updateTotalPrice() {
        if (adapter != null) {
            BigDecimal mainFoodPrice = selectedFood.getDiscountPrice();
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            BigDecimal additionalFoodTotalPrice = adapter.getTotalSelectedPrice();
            BigDecimal totalPriceNoMulti = mainFoodPrice.add(additionalFoodTotalPrice);
            String formattedTotal = formatTotalPrice(totalPriceNoMulti, quantity);

            String currentText = addToCartButton.getText().toString();
            String[] parts = currentText.split(" - ");  // Tách phần trước và sau dấu "-"

            if (parts.length > 1) {
                String newText = parts[0] + " - " + formattedTotal + "đ";  // Chèn giá mới vào sau dấu "-"
                addToCartButton.setText(newText);
            }
        } else {
            Log.e("PopUpFood", "Adapter is null");
        }
    }

    private void init(FoodDTO.GetFoodResponse selectedFood) {
        this.selectedFood = selectedFood;

        foodImage = findViewById(R.id.food_popup_img);
        foodName = findViewById(R.id.food_popup_name);
        foodPrice = findViewById(R.id.food_popup_price);
        foodDes = findViewById(R.id.food_popup_des);
        note = findViewById(R.id.food_popup_note);
        tvQuantity = findViewById(R.id.tv_quantity);
        increaseButton = findViewById(R.id.btn_increase);
        decreaseButton = findViewById(R.id.btn_decrease);
        addToCartButton = findViewById(R.id.btn_add_to_cart);
        lvAdditionalFoods = findViewById(R.id.lvAdditionalFoods);
        Glide.with(this).load(selectedFood.getImage()).into(foodImage);
        foodName.setText(selectedFood.getName());
        Util.handlePriceDisplay(selectedFood, foodPrice);
        foodDes.setText(selectedFood.getDescription());
        tvQuantity.setText("1");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.popup_food);

        selectedFood = (FoodDTO.GetFoodResponse) getIntent().getSerializableExtra("selectedFood");
        Long restaurantId = (Long) getIntent().getSerializableExtra("restaurantId");
        if (selectedFood != null && restaurantId != null) {
            init(selectedFood);
            getAdditionalFoodsOfFood(restaurantId, selectedFood.getId());
        }

        increaseButton.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
            int newValue = currentQuantity + 1;
            tvQuantity.setText(String.valueOf(newValue));
            updateTotalPrice();
        });

        decreaseButton.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
            int newValue = currentQuantity - 1;
            if (newValue > 0) {
                tvQuantity.setText(String.valueOf(newValue));
                updateTotalPrice();
            }
        });
    }

    public void addToCart(View view) {
        CartService cartService = ApiClient.getClient().create(CartService.class);

        CartDTO.AddToCartRequest request = CartDTO.AddToCartRequest.builder()
                .foodId(selectedFood.getId())
                .additionalItems(new ArrayList<>(adapter.getIds()))
                .note(note.getText().toString())
                .quantity(Integer.parseInt(tvQuantity.getText().toString()))
                .build();

        String token = "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwMTExMTExMTExMSIsImlhdCI6MTc0NTg0ODAwMSwiZXhwIjoxNzQ2MDI4MDAxfQ.KGpdpOZqbV7CvL2gyE_zAQvb-CJ_WYeyczi_1QkdVkA"; // Tùy bạn lưu token ở đâu

        cartService.addToCart(token, 1L, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("INFO", "OK");
                    finish();
                } else {
                    Log.e("API", "Error response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }

    public void backToRestaurant(View view) {
        finish();
    }

    private void getAdditionalFoodsOfFood(long restaurantId, long foodId) {
        FoodService foodService = ApiClient.getClient().create(FoodService.class);
        Call<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>> call = foodService.getAdditionalFoodsOfFood(
                foodId, restaurantId, 0, 20, true);

        call.enqueue(new Callback<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>> call,
                                   Response<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodDTO.GetFoodResponse> responseList = response.body().getData().getItems();
                    addiList.clear();
                    if (responseList != null) {
                        addiList.addAll(responseList);
                    }

                    // Tạo Adapter và gán vào ListView
                    adapter = new AdditionalFoodAdapter(PopUpFood.this, addiList);
                    lvAdditionalFoods.setAdapter(adapter);
                    updateTotalPrice();
                } else {
                    Log.e("API", "Error response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<FoodDTO.GetFoodResponse>>>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }
}
