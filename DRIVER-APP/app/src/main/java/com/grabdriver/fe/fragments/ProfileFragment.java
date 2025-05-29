package com.grabdriver.fe.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.grabdriver.fe.R;
import com.grabdriver.fe.activities.LoginActivity;

import java.text.NumberFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextView tvShipperName, tvShipperPhone, tvShipperEmail;
    private TextView tvVehicleType, tvVehicleNumber;
    private TextView tvTotalOrders, tvTotalEarnings, tvRating;
    private Button btnEditProfile;
    private CardView cardLogout;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        loadProfileData();
        setupClickListeners();
        
        return view;
    }

    private void initViews(View view) {
        tvShipperName = view.findViewById(R.id.tv_shipper_name);
        tvShipperPhone = view.findViewById(R.id.tv_shipper_phone);
        tvShipperEmail = view.findViewById(R.id.tv_shipper_email);
        tvVehicleType = view.findViewById(R.id.tv_vehicle_type);
        tvVehicleNumber = view.findViewById(R.id.tv_vehicle_number);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvTotalEarnings = view.findViewById(R.id.tv_total_earnings);
        tvRating = view.findViewById(R.id.tv_rating);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        cardLogout = view.findViewById(R.id.card_logout);
        
        sharedPreferences = requireActivity().getSharedPreferences("GrabDriverPrefs", MODE_PRIVATE);
    }

    private void loadProfileData() {
        // Load data from SharedPreferences
        String shipperName = sharedPreferences.getString("shipperName", "Tài xế");
        String shipperPhone = sharedPreferences.getString("shipperPhone", "");
        String shipperEmail = sharedPreferences.getString("shipperEmail", "");
        String vehicleType = sharedPreferences.getString("vehicleType", "");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "");
        int totalOrders = sharedPreferences.getInt("totalOrders", 0);
        long totalEarnings = sharedPreferences.getLong("totalEarnings", 0);
        float rating = sharedPreferences.getFloat("rating", 0.0f);
        
        // Set profile data
        tvShipperName.setText(shipperName);
        tvShipperPhone.setText(shipperPhone);
        tvShipperEmail.setText(shipperEmail);
        tvVehicleType.setText(vehicleType);
        tvVehicleNumber.setText(vehicleNumber);
        tvTotalOrders.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(totalOrders));
        tvTotalEarnings.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(totalEarnings) + " đ");
        tvRating.setText(String.format(Locale.getDefault(), "%.1f ⭐", rating));
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> showEditEmailDialog());
        
        cardLogout.setOnClickListener(v -> {
            // Clear login state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            
            // Navigate to login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void showEditEmailDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_email, null);
        
        // Initialize dialog views
        TextView tvCurrentName = dialogView.findViewById(R.id.tv_current_name);
        TextView tvCurrentPhone = dialogView.findViewById(R.id.tv_current_phone);
        TextView tvCurrentVehicle = dialogView.findViewById(R.id.tv_current_vehicle);
        TextInputEditText etNewEmail = dialogView.findViewById(R.id.et_new_email);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        
        // Load current data
        String shipperName = sharedPreferences.getString("shipperName", "Tài xế");
        String shipperPhone = sharedPreferences.getString("shipperPhone", "");
        String shipperEmail = sharedPreferences.getString("shipperEmail", "");
        String vehicleType = sharedPreferences.getString("vehicleType", "");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "");
        
        tvCurrentName.setText(shipperName);
        tvCurrentPhone.setText(shipperPhone);
        tvCurrentVehicle.setText(vehicleType + " - " + vehicleNumber);
        etNewEmail.setText(shipperEmail);
        
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            String newEmail = etNewEmail.getText().toString().trim();
            
            if (TextUtils.isEmpty(newEmail)) {
                etNewEmail.setError("Vui lòng nhập email");
                return;
            }
            
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                etNewEmail.setError("Email không hợp lệ");
                return;
            }
            
            // Save new email
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("shipperEmail", newEmail);
            editor.apply();
            
            // Update UI
            tvShipperEmail.setText(newEmail);
            
            Toast.makeText(getContext(), "Cập nhật email thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh profile data when fragment becomes visible
        if (sharedPreferences != null) {
            loadProfileData();
        }
    }
} 