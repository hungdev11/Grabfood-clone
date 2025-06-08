package com.grabdriver.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.grabdriver.myapplication.activities.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.EarningsResponse;
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.models.ProfileStatistics;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.utils.Constants;
import com.grabdriver.myapplication.utils.SessionManager;

public class HomeFragment extends Fragment {
    private Switch onlineSwitch;
    private TextView statusText;
    private TextView todayEarningsText;
    private TextView totalOrdersText;
    private TextView ratingText;
    private Button findOrdersButton;
    private CardView cardViewOrders;
    private CardView cardViewWallet;

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
        cardViewOrders = view.findViewById(R.id.card_view_orders);
        cardViewWallet = view.findViewById(R.id.card_view_wallet);
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

        // Quick Actions Click Listeners
        cardViewOrders.setOnClickListener(v -> {
            // Navigate to Orders Fragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToOrdersTab();
            }
        });

        cardViewWallet.setOnClickListener(v -> {
            // Navigate to Wallet Fragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToWalletTab();
            }
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

            // Lấy thống kê đơn hôm nay từ order API  
            apiManager.getOrderRepository().getTodayOrdersCount(new ApiRepository.NetworkCallback<Integer>() {
                @Override
                public void onSuccess(Integer todayOrdersCount) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (todayOrdersCount != null) {
                                totalOrdersText.setText(todayOrdersCount + " đơn");
                            } else {
                                totalOrdersText.setText("0 đơn");
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            totalOrdersText.setText("0 đơn");
                        });
                    }
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