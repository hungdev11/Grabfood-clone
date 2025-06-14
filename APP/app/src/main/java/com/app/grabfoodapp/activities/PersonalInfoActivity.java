package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.auth.AuthService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.UserInfoResponse;
import com.app.grabfoodapp.dto.request.ChangePasswordRequest;
import com.app.grabfoodapp.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvUsername;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        LinearLayout headerChangePassword = findViewById(R.id.header_change_password);
        LinearLayout contentChangePassword = findViewById(R.id.content_change_password);
        ImageView expandIndicator = findViewById(R.id.iv_expand_indicator);

        headerChangePassword.setOnClickListener(v -> {
            // Toggle visibility
            if (contentChangePassword.getVisibility() == View.VISIBLE) {
                contentChangePassword.setVisibility(View.GONE);
                expandIndicator.setRotation(0); // Point down
            } else {
                contentChangePassword.setVisibility(View.VISIBLE);
                expandIndicator.setRotation(180); // Point up
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông tin tài khoản");
        }

        // Initialize views
        tvName = findViewById(R.id.tv_name);
//        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvUsername = findViewById(R.id.tv_username);

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);


        // Initialize token manager and API service
        tokenManager = new TokenManager(this);
        authService = ApiClient.getClient().create(AuthService.class);

        // Load user information
        loadUserInfo();
        btnChangePassword.setOnClickListener(v -> changePassword());
    }
    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Create request
        ChangePasswordRequest request = new ChangePasswordRequest(
                currentPassword, newPassword, confirmPassword);

        // Make API call
        String token = tokenManager.getToken();
        authService.changePassword("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonalInfoActivity.this,
                            "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                    // Clear password fields
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            try {
                                JSONObject errorJson = new JSONObject(errorBodyStr);
                                String message = errorJson.optString("message", "Đổi mật khẩu thất bại");
                                Toast.makeText(PersonalInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(PersonalInfoActivity.this,
                                        "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        Toast.makeText(PersonalInfoActivity.this,
                                "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadUserInfo() {
        if (!tokenManager.hasToken()) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String token = tokenManager.getToken();
        authService.getUserInfo("Bearer " + token).enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userInfo = response.body();
                    displayUserInfo(userInfo);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            try {
                                JSONObject errorJson = new JSONObject(errorBodyStr);
                                String message = errorJson.optString("message", "Failed to load user info");
                                Toast.makeText(PersonalInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(PersonalInfoActivity.this,
                                        "Failed to load user info", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        Toast.makeText(PersonalInfoActivity.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfo(UserInfoResponse userInfo) {
        tvName.setText(userInfo.getName());
//        tvPhone.setText(userInfo.getPhone());
        tvEmail.setText(userInfo.getEmail());
        tvUsername.setText(userInfo.getUsername());
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