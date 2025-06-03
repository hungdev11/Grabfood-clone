package com.grabdriver.myapplication.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.grabdriver.myapplication.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView nameText;
    private TextView emailText;
    private TextView phoneText;
    private TextView vehicleTypeText;
    private TextView licensePlateText;
    private TextView ratingText;
    private TextView totalOrdersText;
    private TextView completedOrdersText;
    private TextView acceptanceRateText;
    private TextView cancellationRateText;
    private TextView statusText;
    private Button logoutButton;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get SessionManager from MainActivity
        if (getActivity() instanceof MainActivity) {
            sessionManager = ((MainActivity) getActivity()).getSessionManager();
        }

        initViews(view);
        loadProfileData();
        setupLogoutButton();

        return view;
    }

    private void initViews(View view) {
        nameText = view.findViewById(R.id.text_name);
        emailText = view.findViewById(R.id.text_email);
        phoneText = view.findViewById(R.id.text_phone);
        vehicleTypeText = view.findViewById(R.id.text_vehicle_type);
        licensePlateText = view.findViewById(R.id.text_license_plate);
        ratingText = view.findViewById(R.id.text_rating);
        totalOrdersText = view.findViewById(R.id.text_total_orders);
        completedOrdersText = view.findViewById(R.id.text_completed_orders);
        acceptanceRateText = view.findViewById(R.id.text_acceptance_rate);
        cancellationRateText = view.findViewById(R.id.text_cancellation_rate);
        statusText = view.findViewById(R.id.text_status);
        logoutButton = view.findViewById(R.id.btn_logout);
    }

    private void loadProfileData() {
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            Shipper shipper = sessionManager.getShipperInfo();

            if (shipper != null) {
                // Display actual shipper data from session
                nameText.setText(shipper.getName() != null ? shipper.getName() : "N/A");
                emailText.setText(shipper.getEmail() != null ? shipper.getEmail() : "N/A");
                phoneText.setText(shipper.getPhone() != null ? shipper.getPhone() : "N/A");
                vehicleTypeText.setText(shipper.getVehicleType() != null ? shipper.getVehicleType() : "N/A");
                licensePlateText.setText(shipper.getLicensePlate() != null ? shipper.getLicensePlate() : "N/A");
                ratingText.setText(String.format("%.1f ⭐", shipper.getRating()));
                totalOrdersText.setText(String.valueOf(shipper.getTotalOrders()));
                completedOrdersText.setText(String.valueOf(shipper.getCompletedOrders()));
                acceptanceRateText.setText(String.format("%.1f%%", shipper.getAcceptanceRate()));
                cancellationRateText.setText(String.format("%.1f%%", shipper.getCancellationRate()));

                // Set status with appropriate color
                String status = shipper.getStatus() != null ? shipper.getStatus() : "UNKNOWN";
                statusText.setText(status);

                if ("ACTIVE".equals(status)) {
                    statusText.setTextColor(getResources().getColor(R.color.status_online));
                } else {
                    statusText.setTextColor(getResources().getColor(R.color.status_offline));
                }
            }
        } else {
            // Fallback to demo data if session is not available
            loadDemoData();
        }
    }

    private void loadDemoData() {
        nameText.setText("Demo Driver");
        emailText.setText("demo@grabdriver.com");
        phoneText.setText("0901234567");
        vehicleTypeText.setText("Xe máy");
        licensePlateText.setText("29A-12345");
        ratingText.setText("4.8 ⭐");
        totalOrdersText.setText("156");
        completedOrdersText.setText("145");
        acceptanceRateText.setText("95.5%");
        cancellationRateText.setText("2.1%");
        statusText.setText("ACTIVE");
        statusText.setTextColor(getResources().getColor(R.color.status_online));
    }

    private void setupLogoutButton() {
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> showLogoutConfirmation());
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).logout();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}