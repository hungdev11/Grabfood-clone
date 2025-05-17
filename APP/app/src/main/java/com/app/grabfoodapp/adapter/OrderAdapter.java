package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.response.OrderResponse;
import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<OrderResponse> orders;

    public OrderAdapter(Context context, List<OrderResponse> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);

        // Định dạng giá
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvOrderShopName.setText(order.getRestaurantName());
        holder.tvOrderTotalPrice.setText(formatter.format(order.getTotalPrice()) + " đ");
        holder.tvOrderShippingFee.setText(formatter.format(order.getShippingFee()) + " đ");

        // Thiết lập RecyclerView lồng cho chi tiết đơn hàng
        holder.rvOrderItem.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderItem.setNestedScrollingEnabled(false); // Tắt cuộn lồng
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(context, order.getCartDetails());
        holder.rvOrderItem.setAdapter(orderDetailAdapter);

        // Hiển thị/ẩn nút Đánh giá
        if (!order.isReview() && "COMPLETED".equals(order.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);

        } else {
            holder.btnReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderShopName, tvOrderTotalPrice, tvOrderShippingFee;
        RecyclerView rvOrderItem;
        Button btnReview;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderShopName = itemView.findViewById(R.id.tvOrderShopName);
            tvOrderTotalPrice = itemView.findViewById(R.id.tvOrderTotalPrice);
            tvOrderShippingFee = itemView.findViewById(R.id.tvOrderShippingFee);
            rvOrderItem = itemView.findViewById(R.id.rvOrderItem);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}