package com.grabdriver.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.utils.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private static final String TAG = "OrdersFragment";

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressLoading;
    private TextView emptyView;

    private ApiManager apiManager;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        if (getActivity() instanceof MainActivity) {
            apiManager = ((MainActivity) getActivity()).getApiManager();
        }

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadOrders();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressLoading = view.findViewById(R.id.progress_loading);
        emptyView = view.findViewById(R.id.text_empty_orders);
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshOrders);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark
        );
    }

    private void loadOrders() {
        showLoading(true);
        
        if (apiManager != null) {
            apiManager.getOrderRepository().getAssignedOrders(currentPage, PAGE_SIZE, 
                    new ApiRepository.NetworkCallback<OrderResponse>() {
                @Override
                public void onSuccess(OrderResponse result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            
                            if (result != null && result.getOrders() != null && !result.getOrders().isEmpty()) {
                                orderList.clear();
                                // Filter out any null orders from the list
                                for (Order order : result.getOrders()) {
                                    if (order != null) {
                                        orderList.add(order);
                                    }
                                }
                                orderAdapter.notifyDataSetChanged();
                                showEmptyView(orderList.isEmpty());
                            } else {
                                orderList.clear();
                                orderAdapter.notifyDataSetChanged();
                                showEmptyView(true);
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            showEmptyView(true);
                            Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } else {
            showLoading(false);
            showEmptyView(true);
        }
    }

    public void refreshOrders() {
        currentPage = 1;
        loadOrders();
    }

    private void showLoading(boolean isLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
        if (progressLoading != null) {
            progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyView(boolean isEmpty) {
        if (emptyView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onOrderClick(Order order) {
        // Open map activity for the selected order
        Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra("order_id", order.getId());
        intent.putExtra("order", order);
        startActivity(intent);
    }

    @Override
    public void onAcceptOrder(Order order) {
        if (apiManager != null) {
            showLoading(true);
            
            // Lấy thời gian ước tính
            String pickupTime = null; // Thời gian lấy hàng
            String deliveryTime = null; // Thời gian giao hàng
            String note = ""; // Ghi chú
            
            apiManager.getOrderRepository().acceptOrder(
                    order.getId(), 
                    pickupTime, 
                    deliveryTime, 
                    note, 
                    new ApiRepository.NetworkCallback<Order>() {
                @Override
                public void onSuccess(Order result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), "Đã nhận đơn hàng thành công", Toast.LENGTH_SHORT).show();
                            
                            // Cập nhật order trong danh sách
                            int position = orderList.indexOf(order);
                            if (position != -1 && result != null) {
                                orderList.set(position, result);
                                orderAdapter.notifyItemChanged(position);
                            }
                            
                            // Mở bản đồ để điều hướng
                            onOrderClick(result != null ? result : order);
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

    @Override
    public void onRejectOrder(Order order) {
        if (apiManager != null) {
            showLoading(true);
            
            String reason = "Tài xế bận"; // Lý do từ chối
            String note = ""; // Ghi chú
            
            apiManager.getOrderRepository().rejectOrder(
                    order.getId(), 
                    reason, 
                    note, 
                    new ApiRepository.NetworkCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), "Đã từ chối đơn hàng", Toast.LENGTH_SHORT).show();
                            
                            // Xóa order khỏi danh sách
                            orderList.remove(order);
                            orderAdapter.notifyDataSetChanged();
                            
                            // Hiển thị empty view nếu danh sách trống
                            showEmptyView(orderList.isEmpty());
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
    
    @Override
    public void onResume() {
        super.onResume();
        refreshOrders();
    }
}