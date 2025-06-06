package com.grabdriver.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.grabdriver.myapplication.MainActivity;
import com.grabdriver.myapplication.MapActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.OrderAdapter;
import com.grabdriver.myapplication.models.EarningsResponse;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.models.ProfileStatistics;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.utils.Constants;
import com.grabdriver.myapplication.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Switch onlineSwitch;
    private TextView statusText;
    private TextView todayEarningsText;
    private TextView totalOrdersText;
    private TextView ratingText;
    private Button findOrdersButton;

    private SessionManager sessionManager;
    private ApiManager apiManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        
        if (getActivity() instanceof MainActivity) {
            apiManager = ((MainActivity) getActivity()).getApiManager();
        }

        initViews(view);
        setupListeners();
        loadShipperData();

        return view;
    }

    private void initViews(View view) {
        onlineSwitch = view.findViewById(R.id.switch_online);
        statusText = view.findViewById(R.id.text_status);
        todayEarningsText = view.findViewById(R.id.text_today_earnings);
        totalOrdersText = view.findViewById(R.id.text_total_orders);
        ratingText = view.findViewById(R.id.text_rating);
        findOrdersButton = view.findViewById(R.id.btn_find_orders);
    }

    private void setupListeners() {
        onlineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateOnlineStatus(isChecked);

            // Update status in MainActivity
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setOnlineStatus(isChecked);
            }
        });

        findOrdersButton.setOnClickListener(v -> {
            // Tìm kiếm đơn hàng
            Toast.makeText(getContext(), "Đang tìm kiếm đơn hàng...", Toast.LENGTH_SHORT).show();
            loadAvailableOrders();
        });
    }

    private void loadShipperData() {
        // Lấy thông tin cơ bản từ SessionManager
        Shipper shipper = sessionManager.getShipperInfo();
        if (shipper != null) {
            ratingText.setText(String.format("%.1f", shipper.getRating()));
            onlineSwitch.setChecked(sessionManager.isOnline());
            updateOnlineStatus(sessionManager.isOnline());
        }

        // Gọi API lấy thông tin chi tiết tài xế
        if (apiManager != null) {
            apiManager.getProfileRepository().getProfile(new ApiRepository.NetworkCallback<Shipper>() {
                @Override
                public void onSuccess(Shipper result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (result != null) {
                                ratingText.setText(String.format("%.1f", result.getRating()));
                                // Cập nhật các thông tin khác nếu cần
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Error handled silently
                }
            });

            // Lấy thống kê tài khoản
            apiManager.getProfileRepository().getProfileStats(new ApiRepository.NetworkCallback<ProfileStatistics>() {
                @Override
                public void onSuccess(ProfileStatistics result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (result != null) {
                                totalOrdersText.setText(result.getTotalOrders() + " đơn");
                                // Cập nhật các thống kê khác nếu cần
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Error handled silently
                }
            });

            // Lấy thông tin thu nhập hôm nay
            loadTodayEarnings();
        }
    }

    private void loadTodayEarnings() {
        if (apiManager != null) {
            apiManager.getWalletRepository().getEarnings(Constants.Period.TODAY, 
                new ApiRepository.NetworkCallback<EarningsResponse>() {
                    @Override
                    public void onSuccess(EarningsResponse result) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (result != null) {
                                    long todayEarnings = result.getTodayEarnings();
                                    todayEarningsText.setText(String.format("₫%,d", todayEarnings));
                                } else {
                                    todayEarningsText.setText("₫0");
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                todayEarningsText.setText("₫0");
                            });
                        }
                    }
                });
        }
    }

    private void loadAvailableOrders() {
        if (apiManager != null) {
            apiManager.getOrderRepository().getAvailableOrders(1, 10, 
                new ApiRepository.NetworkCallback<OrderResponse>() {
                    @Override
                    public void onSuccess(OrderResponse result) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (result != null && result.getOrders() != null && !result.getOrders().isEmpty()) {
                                    Toast.makeText(getContext(), "Tìm thấy " + result.getOrders().size() + " đơn hàng", Toast.LENGTH_SHORT).show();
                                    
                                    // Chuyển đến tab Orders để hiển thị đơn hàng
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).navigateToOrdersTab();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Không tìm thấy đơn hàng nào", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Lỗi khi tìm đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
        }
    }

    public void refreshOrders() {
        loadTodayEarnings();
    }

    private void updateOnlineStatus(boolean isOnline) {
        if (isOnline) {
            statusText.setText("Đang hoạt động");
            statusText.setTextColor(requireContext().getResources().getColor(R.color.status_online, null));
            findOrdersButton.setEnabled(true);
            findOrdersButton.setText("Tìm đơn hàng");
        } else {
            statusText.setText("Ngoại tuyến");
            statusText.setTextColor(requireContext().getResources().getColor(R.color.status_offline, null));
            findOrdersButton.setEnabled(false);
            findOrdersButton.setText("Offline");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadShipperData();
        refreshOrders();
    }
}