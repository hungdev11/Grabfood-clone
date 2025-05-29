package com.grabdriver.fe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.grabdriver.fe.R;
import com.grabdriver.fe.adapters.OrderAdapter;
import com.grabdriver.fe.data.MockDataManager;
import com.grabdriver.fe.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerViewOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> filteredOrderList;
    
    // Filter chips
    private Chip chipAll, chipPending, chipAccepted, chipDelivering, chipCompleted, chipCancelled, chipRejected;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupFilterChips();
        loadOrders();
        setupSwipeRefresh();
        
        return view;
    }

    private void initViews(View view) {
        recyclerViewOrders = view.findViewById(R.id.recycler_view_orders);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        
        // Initialize filter chips
        chipAll = view.findViewById(R.id.chip_all);
        chipPending = view.findViewById(R.id.chip_pending);
        chipAccepted = view.findViewById(R.id.chip_accepted);
        chipDelivering = view.findViewById(R.id.chip_delivering);
        chipCompleted = view.findViewById(R.id.chip_completed);
        chipCancelled = view.findViewById(R.id.chip_cancelled);
        chipRejected = view.findViewById(R.id.chip_rejected);
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = MockDataManager.createMockOrders();
        filteredOrderList = new ArrayList<>(orderList);
        orderAdapter = new OrderAdapter(filteredOrderList, this);
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupFilterChips() {
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipAll);
                currentFilter = "all";
                filterOrders();
            }
        });

        chipPending.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipPending);
                currentFilter = "pending";
                filterOrders();
            }
        });

        chipAccepted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipAccepted);
                currentFilter = "accepted";
                filterOrders();
            }
        });

        chipDelivering.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipDelivering);
                currentFilter = "delivering";
                filterOrders();
            }
        });

        chipCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipCompleted);
                currentFilter = "completed";
                filterOrders();
            }
        });

        chipCancelled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipCancelled);
                currentFilter = "cancelled";
                filterOrders();
            }
        });

        chipRejected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipRejected);
                currentFilter = "rejected";
                filterOrders();
            }
        });
    }

    private void uncheckOtherChips(Chip selectedChip) {
        Chip[] chips = {chipAll, chipPending, chipAccepted, chipDelivering, chipCompleted, chipCancelled, chipRejected};
        for (Chip chip : chips) {
            if (chip != selectedChip) {
                chip.setChecked(false);
            }
        }
    }

    private void filterOrders() {
        filteredOrderList.clear();
        
        if ("all".equals(currentFilter)) {
            filteredOrderList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                if (currentFilter.equals(order.getStatus())) {
                    filteredOrderList.add(order);
                }
            }
        }
        
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
    }

    private void loadOrders() {
        // Load orders from MockDataManager
        orderList.clear();
        orderList.addAll(MockDataManager.createMockOrders());
        
        // Apply current filter
        filterOrders();
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulate network delay
            swipeRefreshLayout.postDelayed(() -> {
                loadOrders();
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });
        
        // Set refresh colors
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_color,
                R.color.secondary_color,
                R.color.accent_color
        );
    }

    @Override
    public void onOrderClick(Order order) {
        // Handle order click - show order details or actions
        String message = "Đơn hàng #" + order.getId() + " - " + order.getCustomerName();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        
        // TODO: Navigate to order details or show action dialog
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh orders when fragment becomes visible
        loadOrders();
    }
} 