package com.grabdriver.myapplication.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.grabdriver.myapplication.activities.EditProfileActivity;
import com.grabdriver.myapplication.activities.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.ProfileStatistics;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.utils.SessionManager;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int REQUEST_EDIT_PROFILE = 101;

    private ImageView avatarImage;
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
    private Button editProfileButton;
    private ProgressBar progressLoading;

    private SessionManager sessionManager;
    private ApiManager apiManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get SessionManager and ApiManager from MainActivity
        if (getActivity() instanceof MainActivity) {
            sessionManager = ((MainActivity) getActivity()).getSessionManager();
            apiManager = ((MainActivity) getActivity()).getApiManager();
        }

        initViews(view);
        setupListeners();
        loadProfileData();

        return view;
    }

    private void initViews(View view) {
        avatarImage = view.findViewById(R.id.image_avatar);
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
        editProfileButton = view.findViewById(R.id.btn_edit_profile);
        progressLoading = view.findViewById(R.id.progress_loading);
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> showLogoutConfirmation());

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivityForResult(intent, REQUEST_EDIT_PROFILE);
        });

        if (avatarImage != null) {
            avatarImage.setOnClickListener(v -> selectImage());
        }
    }

    private void loadProfileData() {
        showLoading(true);

        // Lấy thông tin cơ bản từ SessionManager
        Shipper shipperInfo = sessionManager != null ? sessionManager.getShipperInfo() : null;
        if (shipperInfo != null) {
            displayShipperInfo(shipperInfo);
        }

        // Gọi API để lấy thông tin mới nhất
        if (apiManager != null) {
            apiManager.getProfileRepository().getProfile(new ApiRepository.NetworkCallback<Shipper>() {
                @Override
                public void onSuccess(Shipper result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            if (result != null) {
                                displayShipperInfo(result);
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            if (shipperInfo == null) {
                                // Chỉ hiển thị thông báo lỗi nếu không có dữ liệu từ SessionManager
                                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            // Chỉ lấy thống kê từ API nếu dữ liệu từ Shipper chưa có hoặc = 0
            // Ưu tiên dữ liệu từ Shipper object vì nó chính xác hơn
            /* Tạm thời comment để ưu tiên dữ liệu từ Shipper
            apiManager.getProfileRepository().getProfileStats(new ApiRepository.NetworkCallback<ProfileStatistics>() {
                @Override
                public void onSuccess(ProfileStatistics result) {
                    if (getActivity() != null && result != null) {
                        getActivity().runOnUiThread(() -> {
                            // Chỉ cập nhật nếu dữ liệu từ Shipper = 0
                            if (shipperInfo == null || 
                                (shipperInfo.getTotalOrders() == 0 && shipperInfo.getCompletedOrders() == 0)) {
                                totalOrdersText.setText(String.valueOf(result.getTotalOrders()));
                                completedOrdersText.setText(String.valueOf(result.getCompletedOrders()));
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Error handled silently
                }
            });
            */
        } else {
            showLoading(false);
        }
    }

    private void displayShipperInfo(Shipper shipper) {
        nameText.setText(shipper.getName() != null ? shipper.getName() : "N/A");
        emailText.setText(shipper.getEmail() != null ? shipper.getEmail() : "N/A");
        phoneText.setText(shipper.getPhone() != null ? shipper.getPhone() : "N/A");
        vehicleTypeText.setText(shipper.getVehicleType() != null ? shipper.getVehicleType() : "N/A");
        licensePlateText.setText(shipper.getLicensePlate() != null ? shipper.getLicensePlate() : "N/A");
        ratingText.setText(String.format("%.1f ⭐", shipper.getRating()));
        
        // Debug: Log dữ liệu thống kê để kiểm tra
        android.util.Log.d("ProfileFragment", "Total Orders: " + shipper.getTotalOrders());
        android.util.Log.d("ProfileFragment", "Completed Orders: " + shipper.getCompletedOrders());
        android.util.Log.d("ProfileFragment", "Acceptance Rate: " + shipper.getAcceptanceRate());
        android.util.Log.d("ProfileFragment", "Cancellation Rate: " + shipper.getCancellationRate());
        
        // Hiển thị số liệu thực từ database - đây là dữ liệu từ shipper table
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

        // Load avatar if available
        if (shipper.getAvatarUrl() != null && !shipper.getAvatarUrl().isEmpty() && avatarImage != null) {
            Glide.with(this)
                .load(shipper.getAvatarUrl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(avatarImage);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadAvatar(selectedImageUri);
            }
        } else if (requestCode == REQUEST_EDIT_PROFILE && resultCode == getActivity().RESULT_OK) {
            // Refresh profile data after successful edit
            loadProfileData();
        }
    }

    private void uploadAvatar(Uri imageUri) {
        if (apiManager != null) {
            showLoading(true);
            
            apiManager.getProfileRepository().uploadAvatar(imageUri, getContext(), 
                    new ApiRepository.NetworkCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), "Ảnh đại diện đã được cập nhật", Toast.LENGTH_SHORT).show();
                            
                            // Hiển thị avatar mới
                            if (result != null && avatarImage != null) {
                                Glide.with(ProfileFragment.this)
                                    .load(result)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.ic_profile)
                                    .circleCrop()
                                    .into(avatarImage);
                            }
                            
                            // Cập nhật thông tin tài xế
                            loadProfileData();
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }

    private void showLoading(boolean show) {
        if (progressLoading != null) {
            progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
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
    
    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }
}