package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.CartAdapter;
import com.app.grabfoodapp.apiservice.cart.CartService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.CartDetailDTO;
import com.app.grabfoodapp.dto.CartResponse;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CartAdapter adapter;
    List<CartDetailDTO> cartItems = new ArrayList<>();
    TextView txtCartRestaurantName;

    TextView txtTotalCartAmount;
    ImageButton btnCartBack;
    Button btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerView);
        txtCartRestaurantName = findViewById(R.id.restaurantCartName);
        txtTotalCartAmount = findViewById(R.id.totalCartAmount);
        btnCartBack = findViewById(R.id.btnCartBack);
        btnOrder = findViewById(R.id.btnOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems);
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {
                updateTotalPrice();
            }
        });
        recyclerView.setAdapter(adapter);

        CartService cartService = ApiClient.getClient().create(CartService.class);

        Call<ApiResponse<CartResponse>>  call = cartService.getCart();

        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null && apiResponse.getData().getListItem() != null) {
                        cartItems.clear();
                        cartItems.addAll(apiResponse.getData().getListItem());
                        txtCartRestaurantName.setText(apiResponse.getData().getRestaurantName());
                        BigDecimal totalPrice = BigDecimal.ZERO;
                        for (CartDetailDTO item: cartItems) {
                            BigDecimal itemPrice = item.getPrice();
                            for (int i = 0; i < item.getAdditionFoods().size(); i++) {
                                itemPrice = itemPrice.add(item.getAdditionFoods().get(i).getPrice());
                            }
                            totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                        }

                        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                        txtTotalCartAmount.setText("Tổng cộng: " + formatter.format(totalPrice) + "đ");
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });

        btnCartBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems == null || cartItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Log dữ liệu để kiểm tra
                Log.d("CartActivity", "Số lượng món trong giỏ hàng: " + cartItems.size());
                for (CartDetailDTO item : cartItems) {
                    Log.d("CartActivity", "Món: " + item.getFoodName() + ", Giá: " + item.getPrice());
                }
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("cartItems", new ArrayList<>(cartItems));
                intent.putExtra("restaurantName", txtCartRestaurantName.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void updateTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetailDTO item : cartItems) {
            BigDecimal itemPrice = item.getPrice();
            for (int i = 0; i < item.getAdditionFoods().size(); i++) {
                itemPrice = itemPrice.add(item.getAdditionFoods().get(i).getPrice());
            }
            totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        txtTotalCartAmount.setText("Tổng cộng: " + formatter.format(totalPrice) + "đ");
    }

}