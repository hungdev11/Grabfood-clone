package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.AdditionalFoodAdapter;
import com.app.grabfoodapp.apiservice.cart.CartService;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.AdditionFood;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.CartDTO;
import com.app.grabfoodapp.dto.CartDetailDTO;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.PageResponse;
import com.app.grabfoodapp.dto.request.CartUpdateRequest;
import com.app.grabfoodapp.utils.TokenManager;
import com.app.grabfoodapp.utils.Util;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private CartDetailDTO selectedCartItem;
    private Long userId;
    private AdditionalFoodAdapter adapter;
    private TokenManager tokenManager;

    private String formatTotalPrice(BigDecimal discountPrice, int quantity) {
        BigDecimal newTotalPrice = discountPrice.multiply(BigDecimal.valueOf(quantity));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(newTotalPrice);
    }
    public void updateTotalPrice() {
        if (adapter != null) {
            BigDecimal mainFoodPrice = BigDecimal.ZERO;
            if (selectedFood != null) {
                mainFoodPrice = selectedFood.getDiscountPrice();
            } else {
                mainFoodPrice = selectedCartItem.getPrice();;
            }

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

    private void init() {
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
    }
    private void initForFood(FoodDTO.GetFoodResponse selectedFood) {
        init();
        this.selectedFood = selectedFood;
        Glide.with(this).load(selectedFood.getImage()).into(foodImage);
        foodName.setText(selectedFood.getName());
        Util.handlePriceDisplay(selectedFood, foodPrice);
        foodDes.setText(selectedFood.getDescription());
        tvQuantity.setText("1");
    }

    private void initForCartItem(CartDetailDTO selectedCartItem) {
        init();

        String currentText = addToCartButton.getText().toString();
        String[] parts = currentText.split(" - ");  // Tách phần trước và sau dấu "-"

        if (parts.length > 1) {
            String newText = "Update cart - " + parts[1];
            addToCartButton.setText(newText);
        }

        Glide.with(this).load(selectedCartItem.getFood_img()).into(foodImage);
        foodName.setText(selectedCartItem.getFoodName());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        foodPrice.setText(decimalFormat.format(selectedCartItem.getPrice()) + "đ");

        note.setText(selectedCartItem.getNote());
        tvQuantity.setText(String.valueOf(selectedCartItem.getQuantity()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.popup_food);

        // Initialize TokenManager once
        tokenManager = new TokenManager(this);
        // for food popup
        selectedFood = (FoodDTO.GetFoodResponse) getIntent().getSerializableExtra("selectedFood");
        Long restaurantId = (Long) getIntent().getSerializableExtra("restaurantId");

        // for cart popup
        selectedCartItem =  (CartDetailDTO) getIntent().getSerializableExtra("selectedCartItem");
        userId =  (Long) getIntent().getSerializableExtra("userId");
        Long restaurantIdFromCart = (Long) getIntent().getSerializableExtra("restaurantIdFromCart");

        if (selectedFood != null && restaurantId != null) {
            initForFood(selectedFood);
            getAdditionalFoodsOfFood(restaurantId, selectedFood.getId());

            adapter = new AdditionalFoodAdapter(PopUpFood.this, addiList);
            lvAdditionalFoods.setAdapter(adapter);
            updateTotalPrice();
        } else if (selectedCartItem != null && userId != null && restaurantIdFromCart != null) {
            initForCartItem(selectedCartItem);
            getAdditionalFoodsOfFood(restaurantIdFromCart, selectedCartItem.getFoodId());

            adapter = new AdditionalFoodAdapter(PopUpFood.this, addiList);
            adapter.setIds(selectedCartItem.getAdditionFoods().stream()
                    .map(AdditionFood::getId)
                    .collect(Collectors.toSet()));
            lvAdditionalFoods.setAdapter(adapter);
            updateTotalPrice();
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

    public void handlePopupButton(View view) {
        if (addToCartButton.getText().toString().contains("Add")) {
            addToCart();
        } else if (addToCartButton.getText().toString().contains("Update")) {
            updateWholeCartItem();
        }
    }
    private void updateWholeCartItem() {
        String authToken = "Bearer " + tokenManager.getToken();
        CartUpdateRequest request = CartUpdateRequest.builder()
                .userId(userId)
                .cartDetailId(selectedCartItem.getId())
                .newQuantity(Integer.parseInt(tvQuantity.getText().toString()))
                .foodId(selectedCartItem.getFoodId())
                .additionFoodIds(new ArrayList<>(adapter.getIds()))
                .build();
        CartService cartService = ApiClient.getClient().create(CartService.class);
        cartService.updateWholeItem(authToken, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PopUpFood.this,"Sửa món thành công!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void addToCart() {

        if (!tokenManager.hasToken()) {
            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class); // Use 'this' instead of 'context'
            startActivity(intent); // Use directly without context
            return;
        }
        CartService cartService = ApiClient.getClient().create(CartService.class);

        CartDTO.AddToCartRequest request = CartDTO.AddToCartRequest.builder()
                .foodId(selectedFood.getId())
                .additionalItems(new ArrayList<>(adapter.getIds()))
                .note(note.getText().toString())
                .quantity(Integer.parseInt(tvQuantity.getText().toString()))
                .build();
        String authToken = "Bearer " + tokenManager.getToken();
        long userId = Long.parseLong(tokenManager.getUserId());

        cartService.addToCart(authToken, userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("INFO", "OK");
                    Toast.makeText(PopUpFood.this,"Thêm món thành công!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
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
                    adapter.notifyDataSetChanged();
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
