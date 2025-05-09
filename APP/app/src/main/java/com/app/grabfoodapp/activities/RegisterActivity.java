package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.auth.AuthService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.LoginResponse;
import com.app.grabfoodapp.dto.request.RegisterRequest;
import com.app.grabfoodapp.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etPhone, etPassword, etName, etConfirmPassword, etEmail;    private Button btnRegister;
    private ImageButton btnBack;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tokenManager = new TokenManager(this);
        authService = ApiClient.getClient().create(AuthService.class);

        etEmail = findViewById(R.id.et_email_register);
        etPhone = findViewById(R.id.et_phone_register);
        etPassword = findViewById(R.id.et_password_register);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etName = findViewById(R.id.et_name);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        btnRegister.setOnClickListener(v -> attemptRegister());
        btnBack.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString().trim();

        // Input validation
        if (phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create register request
        RegisterRequest registerRequest = new RegisterRequest(phone, password, name, email);
        btnRegister.setEnabled(false);

        // Rest of the code remains the same
        authService.register(registerRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            String responseString = response.body().string();
                            try {
                                // Parse JSON response
                                JSONObject jsonResponse = new JSONObject(responseString);
                                String message = jsonResponse.optString("message", "Registration successful");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                // If not valid JSON, use the raw response
                                Toast.makeText(RegisterActivity.this, responseString, Toast.LENGTH_SHORT).show();
                            }

                            // Navigate to LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
                            startActivity(intent);
                            finish(); // Close the current activity
                        }
                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this,
                                "Error reading response: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            try {
                                // Parse JSON response
                                JSONObject errorJson = new JSONObject(errorBodyStr);
                                String message = errorJson.optString("message", "Registration failed");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                // If not JSON, show raw error
                                Toast.makeText(RegisterActivity.this, errorBodyStr, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}