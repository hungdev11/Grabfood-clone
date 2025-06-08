package com.grabdriver.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grabdriver.myapplication.activities.MainActivity;
import com.grabdriver.myapplication.activities.MapActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.OrderAdapter;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private static final String TAG = "OrdersFragment";

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressLoading;
    private TextView emptyView;
    private LinearLayout offlineView;
    private LinearLayout filterContainer;
    private Spinner statusFilterSpinner;

    private ApiManager apiManager;
    private SessionManager sessionManager;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private String currentStatusFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        if (getActivity() instanceof MainActivity) {
            apiManager = ((MainActivity) getActivity()).getApiManager();
        }
        
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupStatusFilter();
        checkOnlineStatusAndLoadOrders();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressLoading = view.findViewById(R.id.progress_loading);
        emptyView = view.findViewById(R.id.text_empty_orders);
        offlineView = view.findViewById(R.id.layout_offline_view);
        filterContainer = view.findViewById(R.id.layout_filter_container);
        statusFilterSpinner = view.findViewById(R.id.spinner_status_filter);
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

    private void setupStatusFilter() {
        // Create filter options with Vietnamese translations
        String[] statusLabels = {
                "Tất cả",
                "Đã hủy",
                "Bị từ chối", 
                "Đang xử lý",
                "Sẵn sàng lấy hàng",
                "Đã hoàn thành"
        };
        
        String[] statusValues = {
                "ALL",
                "CANCELLED",
                "REJECTED",
                "PROCESSING", 
                "READY_FOR_PICKUP",
                "COMPLETED"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, statusLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilterSpinner.setAdapter(adapter);

        statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatusFilter = statusValues[position];
                if (sessionManager.isOnline()) {
                    refreshOrders();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void checkOnlineStatusAndLoadOrders() {
        if (!sessionManager.isOnline()) {
            showOfflineView();
        } else {
            hideOfflineView();
            loadOrders();
        }
    }

    private void loadOrders() {
        if (!sessionManager.isOnline()) {
            showOfflineView();
            return;
        }
        
        showLoading(true);
        
        // Debug logging
        Long shipperId = sessionManager.getShipperId();
        android.util.Log.d(TAG, "Loading orders for shipper ID: " + shipperId + 
                          ", status filter: " + currentStatusFilter +
                          ", online: " + sessionManager.isOnline());
        
        if (apiManager != null) {
            // Get orders filtered by status for current shipper only
            apiManager.getOrderRepository().getAssignedOrdersByStatus(currentPage, PAGE_SIZE, currentStatusFilter,
                    new ApiRepository.NetworkCallback<OrderResponse>() {
                @Override
                public void onSuccess(OrderResponse result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            
                            // Debug logging
                            int orderCount = (result != null && result.getOrders() != null) ? result.getOrders().size() : 0;
                            android.util.Log.d(TAG, "API returned " + orderCount + " orders");
                            
                            if (result != null && result.getOrders() != null && !result.getOrders().isEmpty()) {
                                orderList.clear();
                                // Filter out any null orders from the list
                                Long currentShipperId = sessionManager.getShipperId();
                                for (Order order : result.getOrders()) {
                                    if (order != null) {
                                        // Set shipper ID for logging purposes (not stored in response but implied)
                                        order.setShipperId(currentShipperId);
                                        android.util.Log.d(TAG, "Order #" + order.getId() + 
                                                         " - Status: " + order.getStatus() + 
                                                         " - Shipper: " + order.getShipperId());
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
        checkOnlineStatusAndLoadOrders();
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

    private void showOfflineView() {
        if (offlineView != null) {
            offlineView.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (filterContainer != null) {
            filterContainer.setVisibility(View.GONE);
        }
        showLoading(false);
    }

    private void hideOfflineView() {
        if (offlineView != null) {
            offlineView.setVisibility(View.GONE);
        }
        if (filterContainer != null) {
            filterContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOrderClick(Order order) {
        // Check if order can be handled (PROCESSING or READY_FOR_PICKUP)
        if ("PROCESSING".equals(order.getStatus()) || "READY_FOR_PICKUP".equals(order.getStatus())) {
            // Open map activity for the selected order
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra("order_id", order.getId());
            intent.putExtra("order", order);
            startActivity(intent);
        } else {
            // Show message for orders that cannot be handled
            String statusMessage = getStatusMessage(order.getStatus());
            Toast.makeText(getContext(), "Đơn hàng này " + statusMessage + ". Không thể thực hiện hành động.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusMessage(String status) {
        switch (status) {
            case "CANCELLED":
                return "đã bị hủy";
            case "REJECTED":
                return "đã bị từ chối";
            case "COMPLETED":
                return "đã hoàn thành";
            case "SHIPPING":
                return "đang được giao";
            default:
                return "không khả dụng";
        }
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
        checkOnlineStatusAndLoadOrders();
    }
}