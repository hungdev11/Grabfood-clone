package com.grabdriver.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Order;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);

        void onAcceptOrder(Order order);

        void onRejectOrder(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdText;
        private TextView addressText;
        private TextView statusText;
        private TextView totalPriceText;
        private TextView shippingFeeText;
        private TextView paymentMethodText;
        private TextView orderDateText;
        private TextView noteText;
        private TextView distanceText;
        private TextView estimatedTimeText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.text_order_id);
            addressText = itemView.findViewById(R.id.text_address);
            statusText = itemView.findViewById(R.id.text_status);
            totalPriceText = itemView.findViewById(R.id.text_total_price);
            shippingFeeText = itemView.findViewById(R.id.text_shipping_fee);
            paymentMethodText = itemView.findViewById(R.id.text_payment_method);
            orderDateText = itemView.findViewById(R.id.text_order_date);
            noteText = itemView.findViewById(R.id.text_note);
            distanceText = itemView.findViewById(R.id.text_distance);
            estimatedTimeText = itemView.findViewById(R.id.text_estimated_time);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orderList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Order order) {
            orderIdText.setText("Đơn #" + order.getId());
            addressText.setText(order.getAddress());
            totalPriceText.setText(formatCurrency(order.getTotalPrice().longValue()));
            shippingFeeText.setText("Phí ship: " + formatCurrency(order.getShippingFee().longValue()));
            paymentMethodText.setText(order.getPaymentMethod().equals("COD") ? "Tiền mặt" : "Chuyển khoản");
            orderDateText.setText(dateFormat.format(order.getOrderDate()));

            // Status
            setStatusText(order.getStatus());

            // Note
            if (order.getNote() != null && !order.getNote().isEmpty()) {
                noteText.setVisibility(View.VISIBLE);
                noteText.setText("Ghi chú: " + order.getNote());
            } else {
                noteText.setVisibility(View.GONE);
            }

            // Distance and time
            if (order.getDistance() != null) {
                distanceText.setText(String.format("%.1f km", order.getDistance()));
            }

            if (order.getEstimatedTime() != null && order.getEstimatedTime() > 0) {
                estimatedTimeText.setText(order.getEstimatedTime() + " phút");
            } else {
                estimatedTimeText.setText("");
            }
        }

        private void setStatusText(String status) {
            switch (status) {
                case "PENDING":
                    statusText.setText("Chờ xác nhận");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                    break;
                case "READY_FOR_PICKUP":
                    statusText.setText("Sẵn sàng lấy hàng");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                    break;
                case "SHIPPING":
                    statusText.setText("Đang giao");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_online));
                    break;
                case "COMPLETED":
                    statusText.setText("Hoàn thành");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_completed));
                    break;
                case "CANCELLED":
                    statusText.setText("Đã hủy");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_offline));
                    break;
                default:
                    statusText.setText(status);
                    statusText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private String formatCurrency(long amount) {
            return String.format("%,d₫", amount);
        }
    }
}