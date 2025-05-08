package com.app.grabfoodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.VoucherAdapter;
import com.app.grabfoodapp.apiservice.voucher.VoucherService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.response.VoucherResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherActivity extends AppCompatActivity implements VoucherAdapter.OnVoucherSelectionChangeListener{
    ListView listView;
    VoucherAdapter adapter;
    List<VoucherResponse> vouchers = new ArrayList<>();
    BigDecimal totalPrice;
    ImageButton btnBack;
    Button btnApply;

    private TextView txtSelectedCount;
    public static final String EXTRA_SELECTED_VOUCHERS = "selected_vouchers";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voucher);
        txtSelectedCount = findViewById(R.id.txtSelectedCount);
        listView = findViewById(R.id.listViewVoucher);
        adapter = new VoucherAdapter(this, vouchers, this);
        listView.setAdapter(adapter);
        totalPrice = (BigDecimal) getIntent().getSerializableExtra("total_price");
        btnBack = findViewById(R.id.btnVoucherBack);
        btnApply = findViewById(R.id.btnApplyVoucher);
        btnBack.setOnClickListener(v -> finish());
        btnApply.setOnClickListener(v -> {
            // Get selected vouchers
            Map<String, VoucherResponse> selectedVouchersMap = adapter.getSelectedVouchersByType();
            List<VoucherResponse> selectedVouchers = new ArrayList<>(selectedVouchersMap.values());

            // Create result Intent
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_VOUCHERS, new ArrayList<>(selectedVouchers));

            // Set result and finish
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        init();
    }
    private void init() {
        VoucherService voucherService = ApiClient.getClient().create(VoucherService.class);

        voucherService.getVoucherCanApply(totalPrice).enqueue(new Callback<ApiResponse<List<VoucherResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<VoucherResponse>>> call, Response<ApiResponse<List<VoucherResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<VoucherResponse>> apiResponse = response.body();
                    if(apiResponse.getData() != null) {
                        vouchers.clear();
                        vouchers.addAll(apiResponse.getData());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<VoucherResponse>>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSelectionChanged(int selectedCount) {
        txtSelectedCount.setText("Đã chọn " + selectedCount + " ưu đãi");
    }
}