package com.grabdriver.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.MapActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.OrderAdapter;
import com.grabdriver.myapplication.models.Order;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private static final String TAG = "OrdersFragment";

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        initViews(view);
        setupRecyclerView();
        loadOrders();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        // Load orders from database/API
        // For demo purposes, using static data
        orderList.clear();

        // Sample orders
        Order order1 = new Order(1, "123 Nguyễn Văn Linh, Q.7, TP.HCM", new Date(), "SHIPPING",
                new BigDecimal("150000"), new BigDecimal("25000"), "COD");
        order1.setNote("Gọi điện trước khi giao");
        order1.setEstimatedTime(15);
        order1.setDistance(2.5f);
        order1.setCustomerName("Nguyễn Văn A");
        order1.setCustomerPhone("0123456789");
        order1.setDeliveryLatitude(10.7769);
        order1.setDeliveryLongitude(106.7009);

        Order order2 = new Order(2, "456 Lê Văn Việt, Q.9, TP.HCM", new Date(), "READY_FOR_PICKUP",
                new BigDecimal("89000"), new BigDecimal("20000"), "VNPAY");
        order2.setNote("Để ở bảo vệ");
        order2.setEstimatedTime(20);
        order2.setDistance(3.2f);
        order2.setCustomerName("Trần Thị B");
        order2.setCustomerPhone("0987654321");
        order2.setDeliveryLatitude(10.7589);
        order2.setDeliveryLongitude(106.6819);

        Order order3 = new Order(3, "789 Võ Văn Tần, Q.3, TP.HCM", new Date(), "COMPLETED",
                new BigDecimal("200000"), new BigDecimal("30000"), "COD");
        order3.setNote("");
        order3.setEstimatedTime(0);
        order3.setDistance(1.8f);
        order3.setCustomerName("Lê Văn C");
        order3.setCustomerPhone("0369852147");
        order3.setDeliveryLatitude(10.7829);
        order3.setDeliveryLongitude(106.6959);

        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);

        orderAdapter.notifyDataSetChanged();
    }

    public void refreshOrders() {
        Log.d(TAG, "Refreshing orders");
        loadOrders();
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
        // TODO: Implement accept order logic
        Log.d(TAG, "Accepting order: " + order.getId());

        // Update order status
        order.setStatus("SHIPPING");
        orderAdapter.notifyDataSetChanged();

        // Open map for navigation
        onOrderClick(order);
    }

    @Override
    public void onRejectOrder(Order order) {
        // TODO: Implement reject order logic
        Log.d(TAG, "Rejecting order: " + order.getId());

        // Remove order from list
        orderList.remove(order);
        orderAdapter.notifyDataSetChanged();
    }
}