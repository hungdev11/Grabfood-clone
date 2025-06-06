package com.grabdriver.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.grabdriver.myapplication.models.LoginResponse;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.utils.NetworkUtil;
import com.grabdriver.myapplication.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private MaterialCheckBox cbRememberMe;
    private CircularProgressIndicator progressLoading;

    private SessionManager sessionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn() && sessionManager.isSessionValid()) {
            navigateToMain();
            return;
        }

        initViews();
        setupClickListeners();
        loadSavedCredentials();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        progressLoading = findViewById(R.id.progress_loading);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng quên mật khẩu sẽ được cập nhật sớm", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSavedCredentials() {
        if (sessionManager.isRememberMeEnabled()) {
            etPhone.setText(sessionManager.getShipperPhone());
            cbRememberMe.setChecked(true);
        }
    }

    private void handleLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(phone, password)) {
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Gọi API đăng nhập
        apiManager.getAuthRepository().login(phone, password, cbRememberMe.isChecked(), 
            new ApiRepository.NetworkCallback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse result) {
                    runOnUiThread(() -> {
                        handleLoginSuccess(result);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        handleLoginError(errorMessage);
                    });
                }
            });
    }

    private boolean validateInput(String phone, String password) {
        // Validate phone number
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getString(R.string.error_empty_phone));
            etPhone.requestFocus();
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            etPhone.setError(getString(R.string.error_invalid_phone));
            etPhone.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phone) {
        // Vietnamese phone number validation
        // Should start with 0 and have 10-11 digits
        return phone.matches("^0[0-9]{9,10}$");
    }

    private void handleLoginSuccess(LoginResponse response) {
        showLoading(false);
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        navigateToMain();
    }

    private void handleLoginError(String errorMessage) {
        showLoading(false);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressLoading.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText(getString(R.string.login_loading));
        } else {
            progressLoading.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText(getString(R.string.login_button));
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button on login screen
        super.onBackPressed();
        moveTaskToBack(true);
    }
}