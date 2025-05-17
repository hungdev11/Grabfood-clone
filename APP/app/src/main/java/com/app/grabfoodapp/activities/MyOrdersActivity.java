package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.OrderAdapter;
import com.app.grabfoodapp.apiservice.order.OrderService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.response.OrderResponse;
import com.app.grabfoodapp.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    OrderAdapter adapter;

    List<OrderResponse> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.rvOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Orders");
        }
        init();
    }

    private void init() {
        OrderService orderService = ApiClient.getClient().create(OrderService.class);

        TokenManager tokenManager = new TokenManager(this);
        if (!tokenManager.hasToken()) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String token = tokenManager.getToken();

        Call<List<OrderResponse>> call = orderService.getAllOrders("Bearer " + token);

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders = response.body();
                    adapter = new OrderAdapter(MyOrdersActivity.this, orders);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("APIOrder", "Lỗi mạng hoặc URL: " + t.getMessage());
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