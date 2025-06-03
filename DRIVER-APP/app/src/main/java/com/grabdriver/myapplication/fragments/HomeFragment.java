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
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private Switch onlineSwitch;
    private TextView statusText;
    private TextView todayEarningsText;
    private TextView totalOrdersText;
    private TextView ratingText;
    private Button findOrdersButton;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
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
            // Navigate to orders fragment or refresh orders
            Toast.makeText(getContext(), "Tìm kiếm đơn hàng...", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadShipperData() {
        Shipper shipper = sessionManager.getShipperInfo();
        if (shipper != null) {
            ratingText.setText(String.format("%.1f", shipper.getRating()));
            onlineSwitch.setChecked(sessionManager.isOnline());
            updateOnlineStatus(sessionManager.isOnline());

            // Load today's earnings
            loadTodayEarnings();

            // Load total orders count - use default value since Shipper model doesn't have
            // this field
            totalOrdersText.setText("0 đơn");
        }
    }

    private void loadTodayEarnings() {
        // TODO: Implement API call to get today's earnings
        // For demo purposes, using mock data
        todayEarningsText.setText("₫125,000");
    }

    public void refreshOrders() {
        Log.d(TAG, "Refreshing orders");
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

        // Save online status
        sessionManager.setOnlineStatus(isOnline);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadShipperData();
        refreshOrders();
    }
}