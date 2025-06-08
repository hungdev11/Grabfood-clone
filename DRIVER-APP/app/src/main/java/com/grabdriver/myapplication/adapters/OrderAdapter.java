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
            orderIdText.setText(itemView.getContext().getString(R.string.order_id_prefix, order.getId()));
            
            // Address
            addressText.setText(order.getAddress() != null ? order.getAddress() : 
                itemView.getContext().getString(R.string.order_address_unknown));
            
            // Total price
            if (order.getTotalPrice() != null) {
                totalPriceText.setText(formatCurrency(order.getTotalPrice().longValue()));
            } else {
                totalPriceText.setText(itemView.getContext().getString(R.string.order_price_default));
            }
            
            // Shipping fee
            if (order.getShippingFee() != null) {
                String formattedFee = formatCurrency(order.getShippingFee().longValue());
                shippingFeeText.setText(itemView.getContext().getString(R.string.order_shipping_fee_prefix, formattedFee));
            } else {
                shippingFeeText.setText(itemView.getContext().getString(R.string.order_shipping_fee_default));
            }
            
            // Payment method
            String paymentMethod = order.getPaymentMethod();
            if (paymentMethod != null) {
                if (paymentMethod.equals("COD")) {
                    paymentMethodText.setText(itemView.getContext().getString(R.string.order_payment_cod));
                } else {
                    paymentMethodText.setText(itemView.getContext().getString(R.string.order_payment_transfer));
                }
            } else {
                paymentMethodText.setText(itemView.getContext().getString(R.string.order_payment_unknown));
            }
            
            // Order date
            if (order.getOrderDate() != null) {
                orderDateText.setText(dateFormat.format(order.getOrderDate()));
            } else {
                orderDateText.setText(itemView.getContext().getString(R.string.order_date_unknown));
            }

            // Status
            setStatusText(order.getStatus());

            // Note
            if (order.getNote() != null && !order.getNote().isEmpty()) {
                noteText.setVisibility(View.VISIBLE);
                noteText.setText(itemView.getContext().getString(R.string.order_note_prefix, order.getNote()));
            } else {
                noteText.setVisibility(View.GONE);
            }

            // Distance and time
            if (order.getDistance() != null) {
                distanceText.setText(itemView.getContext().getString(R.string.order_distance_format, order.getDistance()));
            } else {
                distanceText.setText(itemView.getContext().getString(R.string.order_data_unknown));
            }

            if (order.getEstimatedTime() != null && order.getEstimatedTime() > 0) {
                estimatedTimeText.setText(itemView.getContext().getString(R.string.order_time_format, order.getEstimatedTime()));
            } else {
                estimatedTimeText.setText(itemView.getContext().getString(R.string.order_data_unknown));
            }
        }

        private void setStatusText(String status) {
            if (status == null) {
                statusText.setText(itemView.getContext().getString(R.string.order_status_unknown));
                statusText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                return;
            }
            
            switch (status) {
                case "PENDING":
                    statusText.setText(itemView.getContext().getString(R.string.order_status_pending));
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                    break;
                case "READY_FOR_PICKUP":
                    statusText.setText(itemView.getContext().getString(R.string.order_status_ready_pickup));
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                    break;
                case "SHIPPING":
                    statusText.setText(itemView.getContext().getString(R.string.order_status_shipping));
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_online));
                    break;
                case "COMPLETED":
                    statusText.setText(itemView.getContext().getString(R.string.order_status_completed));
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_completed));
                    break;
                case "CANCELLED":
                    statusText.setText(itemView.getContext().getString(R.string.order_status_cancelled));
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