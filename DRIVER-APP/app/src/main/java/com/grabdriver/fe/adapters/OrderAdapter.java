package com.grabdriver.fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grabdriver.fe.R;
import com.grabdriver.fe.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.tvOrderId.setText("#" + order.getId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvRestaurantName.setText(order.getRestaurantName());
        holder.tvDeliveryAddress.setText(order.getDeliveryAddress());
        holder.tvTotalPrice.setText(order.getFormattedPrice());
        holder.tvShippingFee.setText(order.getFormattedShippingFee());
        holder.tvDistance.setText(String.format("%.1f km", order.getDistance()));
        holder.tvEstimatedTime.setText(order.getEstimatedTime() + " phút");
        holder.tvGemsEarned.setText("+" + order.getGemsEarned() + " ngọc");
        
        // Show payment type
        holder.tvPaymentType.setText(order.getPaymentTypeDisplay());
        
        // Show earning breakdown for completed orders
        if ("completed".equals(order.getStatus()) || "delivering".equals(order.getStatus())) {
            holder.layoutEarningInfo.setVisibility(View.VISIBLE);
            holder.tvNetEarning.setText("Thực nhận: " + order.getFormattedNetEarning());
            
            if (order.getTip() > 0) {
                holder.tvTipAmount.setVisibility(View.VISIBLE);
                holder.tvTipAmount.setText("Tip: " + order.getFormattedTip());
            } else {
                holder.tvTipAmount.setVisibility(View.GONE);
            }
            
            // Show commission info
            long commission = order.getCommissionAmount();
            holder.tvCommission.setText("Chiết khấu: -" + String.format("%,d đ", commission));
        } else {
            holder.layoutEarningInfo.setVisibility(View.GONE);
        }
        
        // Set status
        String statusText = getStatusText(order.getStatus());
        holder.tvStatus.setText(statusText);
        
        // Set status color
        int statusColor = getStatusColor(order.getStatus(), holder.itemView);
        holder.tvStatus.setTextColor(statusColor);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order);
            }
        });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "Chờ nhận";
            case "accepted":
                return "Đã nhận";
            case "picked_up":
                return "Đã lấy hàng";
            case "delivering":
                return "Đang giao";
            case "completed":
                return "Hoàn thành";
            case "cancelled":
                return "Đã hủy";
            // Keep old uppercase for compatibility
            case "PENDING":
                return "Chờ nhận";
            case "ACCEPTED":
                return "Đã nhận";
            case "PICKED_UP":
                return "Đã lấy hàng";
            case "DELIVERING":
                return "Đang giao";
            case "COMPLETED":
                return "Hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private int getStatusColor(String status, View itemView) {
        switch (status) {
            case "pending":
            case "PENDING":
                return itemView.getContext().getColor(R.color.status_pending);
            case "accepted":
            case "ACCEPTED":
                return itemView.getContext().getColor(R.color.status_accepted);
            case "picked_up":
            case "PICKED_UP":
                return itemView.getContext().getColor(R.color.status_picked_up);
            case "delivering":
            case "DELIVERING":
                return itemView.getContext().getColor(R.color.status_delivering);
            case "completed":
            case "COMPLETED":
                return itemView.getContext().getColor(R.color.status_completed);
            case "cancelled":
            case "CANCELLED":
                return itemView.getContext().getColor(R.color.status_cancelled);
            default:
                return itemView.getContext().getColor(R.color.text_secondary);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvRestaurantName, tvDeliveryAddress;
        TextView tvTotalPrice, tvShippingFee, tvDistance, tvEstimatedTime, tvGemsEarned, tvStatus;
        TextView tvPaymentType, tvNetEarning, tvTipAmount, tvCommission;
        View layoutEarningInfo;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvRestaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvDeliveryAddress = itemView.findViewById(R.id.tv_delivery_address);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvShippingFee = itemView.findViewById(R.id.tv_shipping_fee);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvEstimatedTime = itemView.findViewById(R.id.tv_estimated_time);
            tvGemsEarned = itemView.findViewById(R.id.tv_gems_earned);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPaymentType = itemView.findViewById(R.id.tv_payment_type);
            tvNetEarning = itemView.findViewById(R.id.tv_net_earning);
            tvTipAmount = itemView.findViewById(R.id.tv_tip_amount);
            tvCommission = itemView.findViewById(R.id.tv_commission);
            layoutEarningInfo = itemView.findViewById(R.id.layout_earning_info);
        }
    }
} 