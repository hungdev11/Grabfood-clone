package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.auth.AuthService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.UserInfoResponse;
import com.app.grabfoodapp.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private TextView tvName, tvPhone, tvEmail, tvUsername;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Personal Information");
        }

        // Initialize views
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvUsername = findViewById(R.id.tv_username);

        // Initialize token manager and API service
        tokenManager = new TokenManager(this);
        authService = ApiClient.getClient().create(AuthService.class);

        // Load user information
        loadUserInfo();
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
        tvPhone.setText(userInfo.getPhone());
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