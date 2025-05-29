package com.grabdriver.fe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grabdriver.fe.MainActivity;
import com.grabdriver.fe.R;
import com.grabdriver.fe.data.MockDataManager;
import com.grabdriver.fe.models.Shipper;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvDemoInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("GrabDriverPrefs", MODE_PRIVATE);
        
        // Check if already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMain();
            return;
        }

        initViews();
        setupClickListeners();
        showDemoInfo();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvDemoInfo = findViewById(R.id.tv_demo_info);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDemoInfo() {
        String demoText = "🎯 DEMO LOGIN (Số điện thoại + Mật khẩu):\n" +
                "📱 " + MockDataManager.DEMO_PHONE + " | 🔑 " + MockDataManager.DEMO_PASSWORD + "\n" +
                "📱 " + MockDataManager.DEMO_PHONE2 + " | 🔑 " + MockDataManager.DEMO_PASSWORD2 + "\n" +
                "📱 " + MockDataManager.DEMO_PHONE3 + " | 🔑 " + MockDataManager.DEMO_PASSWORD3;
        tvDemoInfo.setText(demoText);
    }

    private void performLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            etPhone.setError("Số điện thoại không hợp lệ");
            etPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Validate with mock data
        if (MockDataManager.validateLogin(phone, password)) {
            // Save login state and shipper data
            saveShipperData(phone);
            
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Số điện thoại hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // Simple Vietnamese phone number validation
        return phone.matches("^(0[3|5|7|8|9])+([0-9]{8})$");
    }

    private void saveShipperData(String phone) {
        Shipper shipper = MockDataManager.createMockShipper(phone);
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("phone", phone);
        editor.putString("shipperId", shipper.getId());
        editor.putString("shipperName", shipper.getName());
        editor.putString("shipperPhone", shipper.getPhone());
        editor.putString("shipperEmail", shipper.getEmail());
        editor.putString("vehicleType", shipper.getVehicleType());
        editor.putString("vehicleNumber", shipper.getVehicleNumber());
        editor.putInt("gems", shipper.getGems());
        editor.putFloat("rating", shipper.getRating());
        editor.putFloat("acceptanceRate", shipper.getAcceptanceRate());
        editor.putFloat("cancellationRate", shipper.getCancellationRate());
        editor.putInt("totalOrders", shipper.getTotalOrders());
        editor.putLong("totalEarnings", shipper.getTotalEarnings());
        editor.putBoolean("isOnline", shipper.getIsOnline());
        editor.apply();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 