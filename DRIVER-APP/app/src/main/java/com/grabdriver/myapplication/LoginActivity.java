package com.grabdriver.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.grabdriver.myapplication.models.LoginRequest;
import com.grabdriver.myapplication.models.LoginResponse;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private MaterialCheckBox cbRememberMe;
    private CircularProgressIndicator progressLoading;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

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
            // TODO: Implement forgot password functionality
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

        showLoading(true);

        // Create login request
        LoginRequest loginRequest = new LoginRequest(phone, password);

        // TODO: Replace with actual API call
        // For now, simulate login with mock data
        simulateLogin(loginRequest);
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

    private void simulateLogin(LoginRequest request) {
        // This simulates an API call - replace with actual API implementation
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(2000);

                runOnUiThread(() -> {
                    // Mock successful login for testing
                    // In real implementation, this will come from API response
                    if (isValidCredentials(request.getPhone(), request.getPassword())) {
                        // Create mock shipper data (this will come from API)
                        Shipper mockShipper = createMockShipper(request.getPhone());

                        // Create mock login response
                        LoginResponse response = new LoginResponse("mock_jwt_token", mockShipper.getId(), mockShipper);
                        response.setSuccess(true);
                        response.setMessage("Đăng nhập thành công");

                        handleLoginSuccess(response);
                    } else {
                        handleLoginError(getString(R.string.error_login_failed));
                    }
                });
            } catch (InterruptedException e) {
                runOnUiThread(() -> handleLoginError(getString(R.string.error_network)));
            }
        }).start();
    }

    private boolean isValidCredentials(String phone, String password) {
        // Mock validation - replace with actual API validation
        // For testing, accept any valid phone number with password "123456"
        return isValidPhoneNumber(phone) && "123456".equals(password);
    }

    private Shipper createMockShipper(String phone) {
        // Create mock shipper data based on phone (this will come from API)
        Shipper shipper = new Shipper();
        shipper.setId(1L);
        shipper.setName("Shipper " + phone);
        shipper.setPhone(phone);
        shipper.setEmail(phone + "@grabdriver.com");
        shipper.setRating(4.8);
        shipper.setStatus("ACTIVE");
        shipper.setVehicleType("Xe máy");
        shipper.setLicensePlate("29A-12345");
        shipper.setOnline(true);
        shipper.setTotalOrders(156);
        shipper.setCompletedOrders(145);
        shipper.setAcceptanceRate(95.5f);
        shipper.setGems(150);

        return shipper;
    }

    private void handleLoginSuccess(LoginResponse response) {
        showLoading(false);

        // Save session
        sessionManager.createLoginSession(
                response.getToken(),
                response.getShipperInfo(),
                cbRememberMe.isChecked());

        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        // Navigate to main activity
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