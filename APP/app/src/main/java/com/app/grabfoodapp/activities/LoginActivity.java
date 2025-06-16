package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.auth.AuthService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.LoginResponse;
import com.app.grabfoodapp.dto.request.GoogleTokenRequest;
import com.app.grabfoodapp.dto.request.LoginRequest;
import com.app.grabfoodapp.dto.response.GoogleLoginResponse;
import com.app.grabfoodapp.utils.TokenManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TokenManager tokenManager;
    private TextView tvRegister;
    private AuthService authService;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

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

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build GoogleSignInClient with the options
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the sign-in button with proper error handling
        findViewById(R.id.custom_google_sign_in).setOnClickListener(view -> signIn());
    }

    private void signIn() {
        try {
            // Clear any previous sign-in state first
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Now start the new sign-in flow
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error starting Google Sign-In: " + e.getMessage());
            Toast.makeText(this, "Không thể khởi động đăng nhập Google", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Log successful sign-in
            Log.d(TAG, "Google sign-in successful, ID: " + account.getId());

            if (account.getIdToken() != null) {
                String idToken = account.getIdToken();
                Log.d(TAG, "Token obtained successfully");
                sendTokenToBackend(idToken);
            } else {
                Log.e(TAG, "ID token is null");
                Toast.makeText(this, "Không thể lấy token xác thực", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            // Provide more specific error messages based on status code
            String errorMessage;
            switch (e.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    errorMessage = "Đăng nhập đã bị hủy";
                    break;
                case GoogleSignInStatusCodes.NETWORK_ERROR:
                    errorMessage = "Lỗi kết nối mạng";
                    break;
                default:
                    errorMessage = "Đăng nhập thất bại: " + e.getMessage();
            }
            Log.e(TAG, "Google sign-in failed: " + e.getStatusCode());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTokenToBackend(String idToken) {
        GoogleTokenRequest googleTokenRequest = new GoogleTokenRequest(idToken);
        authService.googleLogin(googleTokenRequest).enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(Call<GoogleLoginResponse> call, Response<GoogleLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save the token
                    String token = response.body().getData().getToken();
                    tokenManager.saveToken(token);

                    // Navigate to main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GoogleLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
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