package com.grabdriver.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editEmail;
    private TextView textName;
    private TextView textPhone;
    private TextView textVehicleType;
    private TextView textLicensePlate;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiManager apiManager;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);

        initViews();
        setupListeners();
        loadCurrentData();
    }

    private void initViews() {
        editEmail = findViewById(R.id.edit_email);
        textName = findViewById(R.id.text_name);
        textPhone = findViewById(R.id.text_phone);
        textVehicleType = findViewById(R.id.text_vehicle_type);
        textLicensePlate = findViewById(R.id.text_license_plate);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chỉnh sửa hồ sơ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveEmailChanges());
        cancelButton.setOnClickListener(v -> finish());

        // Show security dialog when user tries to edit readonly fields
        textName.setOnClickListener(v -> showSecurityDialog("tên"));
        textPhone.setOnClickListener(v -> showSecurityDialog("số điện thoại"));
        textVehicleType.setOnClickListener(v -> showSecurityDialog("loại xe"));
        textLicensePlate.setOnClickListener(v -> showSecurityDialog("biển số xe"));
    }

    private void loadCurrentData() {
        Shipper shipper = sessionManager.getShipperInfo();
        if (shipper != null) {
            originalEmail = shipper.getEmail();
            editEmail.setText(originalEmail);
            textName.setText(shipper.getName() != null ? shipper.getName() : "N/A");
            textPhone.setText(shipper.getPhone() != null ? shipper.getPhone() : "N/A");
            textVehicleType.setText(shipper.getVehicleType() != null ? shipper.getVehicleType() : "N/A");
            textLicensePlate.setText(shipper.getLicensePlate() != null ? shipper.getLicensePlate() : "N/A");
        }
    }

    private void showSecurityDialog(String fieldName) {
        new AlertDialog.Builder(this)
                .setTitle("Bảo mật thông tin")
                .setMessage("Vì lý do bảo mật, bạn không thể thay đổi " + fieldName + " trực tiếp.\n\n" +
                           "Để thay đổi thông tin này, vui lòng liên hệ quản trị viên qua:")
                .setPositiveButton("Gọi hotline", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:1900123456"));
                    startActivity(callIntent);
                })
                .setNegativeButton("Gửi email", (dialog, which) -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:support@grabdriver.com"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu thay đổi " + fieldName);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, 
                        "Kính gửi quản trị viên,\n\n" +
                        "Tôi muốn thay đổi " + fieldName + " trong hồ sơ của mình.\n" +
                        "Thông tin hiện tại: " + getFieldValue(fieldName) + "\n\n" +
                        "Vui lòng hỗ trợ tôi thực hiện thay đổi này.\n\n" +
                        "Trân trọng,\n" +
                        sessionManager.getShipperInfo().getName());
                    
                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(emailIntent);
                    } else {
                        Toast.makeText(this, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Đóng", null)
                .show();
    }

    private String getFieldValue(String fieldName) {
        switch (fieldName) {
            case "tên": return textName.getText().toString();
            case "số điện thoại": return textPhone.getText().toString();
            case "loại xe": return textVehicleType.getText().toString();
            case "biển số xe": return textLicensePlate.getText().toString();
            default: return "N/A";
        }
    }

    private void saveEmailChanges() {
        String newEmail = editEmail.getText().toString().trim();
        
        if (newEmail.isEmpty()) {
            editEmail.setError("Email không được để trống");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editEmail.setError("Email không hợp lệ");
            return;
        }

        if (newEmail.equals(originalEmail)) {
            Toast.makeText(this, "Email không có thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar(true);

        apiManager.getProfileRepository().updateEmail(newEmail, new ApiRepository.NetworkCallback<Shipper>() {
            @Override
            public void onSuccess(Shipper result) {
                runOnUiThread(() -> {
                    showProgressBar(false);
                    Toast.makeText(EditProfileActivity.this, "Cập nhật email thành công", Toast.LENGTH_SHORT).show();
                    
                    // Update session data
                    if (result != null) {
                        sessionManager.updateShipperInfo(result);
                    }
                    
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showProgressBar(false);
                    
                    String displayMessage = "Lỗi: " + errorMessage;
                    if (errorMessage.toLowerCase().contains("email already exists")) {
                        displayMessage = "Email này đã được sử dụng bởi tài khoản khác";
                    } else if (errorMessage.toLowerCase().contains("invalid email")) {
                        displayMessage = "Định dạng email không hợp lệ";
                    }
                    
                    Toast.makeText(EditProfileActivity.this, displayMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        saveButton.setEnabled(!show);
        editEmail.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        String currentEmail = editEmail.getText().toString().trim();
        if (!currentEmail.equals(originalEmail)) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có thay đổi chưa được lưu. Bạn có muốn thoát không?")
                    .setPositiveButton("Thoát", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Ở lại", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
} 