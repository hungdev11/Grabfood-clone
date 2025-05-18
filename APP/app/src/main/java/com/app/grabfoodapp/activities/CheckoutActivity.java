package com.app.grabfoodapp.activities;

import android.annotation.SuppressLint;
import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.CartAdapter;
import com.app.grabfoodapp.apiservice.location.LocationService;
import com.app.grabfoodapp.apiservice.order.OrderService;
import com.app.grabfoodapp.apiservice.payment.PaymentService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.CartDetailDTO;
import com.app.grabfoodapp.dto.request.ApplyVoucherRequest;
import com.app.grabfoodapp.dto.request.CreateOrderRequest;
import com.app.grabfoodapp.dto.response.ApplyVoucherResponse;
import com.app.grabfoodapp.dto.response.OrderResponse;
import com.app.grabfoodapp.dto.response.VoucherResponse;
import com.app.grabfoodapp.utils.LocationStorage;
import com.app.grabfoodapp.utils.TokenManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    List<CartDetailDTO> cartItems = new ArrayList<>();
    String restaurantName;

    TextView txtRestaurant;
    RecyclerView recyclerView;
    CartAdapter adapter;
    TextView txtTotalCartAmount;
    TextView deliveryFeeText;
    TextView totalPriceText;
    TextView discountAmount;

    TextView txtShippingAddress;
    TextView discountShipping;
    LinearLayout voucherLayout;
    BigDecimal totalPrice = BigDecimal.ZERO;
    private static final int REQUEST_CODE_VOUCHER = 100;
    private Long cartId;

    private List<String> voucherCodes = new ArrayList<>();

    private String location = "N/A";
    Button btnPayment;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        btnPayment = findViewById(R.id.btn_payment);
        voucherLayout  = findViewById(R.id.discount_layout);
        txtTotalCartAmount = findViewById(R.id.txtTotalCartAmount);
        deliveryFeeText = findViewById(R.id.delivery_fee_text);
        totalPriceText = findViewById(R.id.total_price_checkout);
        discountAmount = findViewById(R.id.txtDiscountAmount);
        discountShipping = findViewById(R.id.txtDiscountShipping);
        txtShippingAddress = findViewById(R.id.txtShippingAddress);
        cartItems = (ArrayList<CartDetailDTO>) getIntent().getSerializableExtra("cartItems");
        cartId = getIntent().getLongExtra("cartId", 0);
        updateTotalPrice();
        restaurantName = getIntent().getStringExtra("restaurantName");
        // Kiểm tra dữ liệu nhận được
        if (cartItems == null || cartItems.isEmpty()) {
            Log.e("CheckoutActivity", "Không nhận được cartItems hoặc danh sách rỗng");
            Toast.makeText(this, "Không có dữ liệu giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Log dữ liệu để kiểm tra
        Log.d("CheckoutActivity", "Số lượng món nhận được: " + cartItems.size());
        for (CartDetailDTO item : cartItems) {
            Log.d("CheckoutActivity", "Món: " + item.getFoodName() + ", Giá: " + item.getPrice());
        }
        restaurantName = getIntent().getStringExtra("restaurantName");
        recyclerView = findViewById(R.id.recyclerViewCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems);
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {
                updateTotalPrice();
            }
        });
        recyclerView.setAdapter(adapter);
        txtRestaurant = findViewById(R.id.checkoutRestaurant);
        txtRestaurant.setText(restaurantName);

        voucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, VoucherActivity.class);
                intent.putExtra("total_price", totalPrice);
                startActivityForResult(intent, REQUEST_CODE_VOUCHER);
            }
        });
        getShippingAddress();
        handleBtnPaymentClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VOUCHER && resultCode == RESULT_OK && data != null) {
            // Retrieve selected vouchers
            ArrayList<VoucherResponse> selectedVouchers = (ArrayList<VoucherResponse>) data.getSerializableExtra(VoucherActivity.EXTRA_SELECTED_VOUCHERS);
            if (selectedVouchers != null) {
                for (VoucherResponse voucher : selectedVouchers) {
                    voucherCodes.add(voucher.getCode());
                }

                ApplyVoucherRequest request = ApplyVoucherRequest.builder()
                        .listCode(voucherCodes)
                        .shippingFee(BigDecimal.valueOf(25000))
                        .totalPrice(totalPrice)
                        .build();
                applyVoucher(request);

            }
        }
    }

    private void applyVoucher(ApplyVoucherRequest request) {
        OrderService orderService = ApiClient.getClient().create(OrderService.class);
        Call<ApiResponse<ApplyVoucherResponse>> call = orderService.checkApplyVoucher(request);
        call.enqueue(new Callback<ApiResponse<ApplyVoucherResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApplyVoucherResponse>> call, Response<ApiResponse<ApplyVoucherResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ApplyVoucherResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null) {
                        ApplyVoucherResponse voucherResponse = apiResponse.getData();
                        Toast.makeText(CheckoutActivity.this, "Đã áp dụng mã giảm giá", Toast.LENGTH_SHORT).show();
                        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                        txtTotalCartAmount.setText(formatter.format(voucherResponse.getNewOrderPrice()) + "đ");
                        deliveryFeeText.setText(formatter.format(voucherResponse.getNewShippingFee()) + "đ");
                        totalPriceText.setText(formatter.format(voucherResponse.getNewShippingFee().add(voucherResponse.getNewOrderPrice())) + "đ");
                        if (voucherResponse.getDiscountOrderPrice().compareTo(BigDecimal.ZERO) > 0) {
                            discountAmount.setText("( -" + formatter.format(voucherResponse.getDiscountOrderPrice()) + "đ )");
                        } else {
                            discountAmount.setText("");

                        }
                        if (voucherResponse.getDiscountShippingPrice().compareTo(BigDecimal.ZERO) > 0) {
                            discountShipping.setText("( -" + formatter.format(voucherResponse.getDiscountShippingPrice()) + "đ )");
                        } else {
                            discountShipping.setText("");
                        }
                    }
                }
                else {
                    Log.e("API", "Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApplyVoucherResponse>> call, Throwable t) {
                Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
            }
        });
    }

    private void updateTotalPrice() {
        totalPrice = BigDecimal.ZERO;
        for (CartDetailDTO item : cartItems) {
            BigDecimal itemPrice = item.getPrice();
            for (int i = 0; i < item.getAdditionFoods().size(); i++) {
                itemPrice = itemPrice.add(item.getAdditionFoods().get(i).getPrice());
            }
            totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        txtTotalCartAmount.setText(formatter.format(totalPrice) + "đ");
        deliveryFeeText.setText("25.000đ");
        totalPriceText.setText(formatter.format(totalPrice.add(BigDecimal.valueOf(25000))) + "đ");
        discountAmount.setText("");
        discountShipping.setText("");
    }

    private void handleBtnPaymentClick() {
        btnPayment.setOnClickListener(new View.OnClickListener() {
            TokenManager tokenManager = new TokenManager(CheckoutActivity.this);

            @Override
            public void onClick(View v) {
                if (!tokenManager.hasToken()) {
                    Toast.makeText(CheckoutActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                String token = tokenManager.getToken();
                CreateOrderRequest request = CreateOrderRequest.builder()
                        .cartId(cartId)
                        .note("App Order")
                        .address(location)
                        .shippingFee(BigDecimal.valueOf(25000))
                        .voucherCode(voucherCodes)
                        .build();


                PaymentService paymentService = ApiClient.getClient().create(PaymentService.class);
                Call<ApiResponse<OrderResponse>> call = paymentService.createCodOrder("Bearer " + token, request);
                call.enqueue(new Callback<ApiResponse<OrderResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<OrderResponse>> call, Response<ApiResponse<OrderResponse>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            ApiResponse<OrderResponse> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                Toast.makeText(CheckoutActivity.this, "Đặt đơn thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<OrderResponse>> call, Throwable t) {
                        Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
                    }
                });
            }
        });
    }

    public void getShippingAddress() {
        double lat = LocationStorage.getLatitude(this);
        double lon = LocationStorage.getLongitude(this);
        LocationService locationService = ApiClient.getClient().create(LocationService.class);
        Call<ResponseBody> call = locationService.getLocation(lat, lon);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("CHeckoutActivity", "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    try {
                        location = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    txtShippingAddress.setText(location);
                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi lấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Lỗi gọi api", Toast.LENGTH_SHORT).show();
                Log.e("APICheckout", "Lỗi mạng hoặc URL: " + t.getMessage());
            }

        });

    }
}