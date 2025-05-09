package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.auth.AuthService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.LoginResponse;
import com.app.grabfoodapp.dto.request.LoginRequest;
import com.app.grabfoodapp.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TokenManager tokenManager;
    private TextView tvRegister;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);
        authService = ApiClient.getClient().create(AuthService.class);

        // Check if already logged in
        if (tokenManager.hasToken()) {
            navigateToMain();
            finish();
        }

        tvRegister = findViewById(R.id.tv_register);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter phone and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(phone, password);
        btnLogin.setEnabled(false); // Prevent multiple clicks

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    // The token from server is already in format "userId#token"
                    tokenManager.saveToken(loginResponse.getToken());

                    // We can now retrieve the user ID separately if needed
                    String username = loginResponse.getUsername();

                    Toast.makeText(LoginActivity.this,
                            "Welcome " + username,
                            Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    try {
                        // Parse error response
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            try {
                                JSONObject errorJson = new JSONObject(errorBodyStr);
                                String errorMessage = errorJson.optString("message", "Login failed");
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, "Login failed: Invalid credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}