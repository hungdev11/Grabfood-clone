package com.grabdriver.fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.grabdriver.fe.R;
import com.grabdriver.fe.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private SimpleDateFormat dateFormat;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        
        // Basic info
        holder.tvTransactionType.setText(transaction.getTypeDisplayName());
        holder.tvDescription.setText(transaction.getDescription());
        holder.tvAmount.setText(transaction.getFormattedAmount());
        holder.tvDate.setText(dateFormat.format(transaction.getTransactionDate()));
        
        // Set amount color based on transaction type
        int amountColor;
        if (transaction.isPositiveTransaction()) {
            amountColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.success_color);
        } else {
            amountColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.error_color);
        }
        holder.tvAmount.setTextColor(amountColor);
        
        // Show detailed breakdown for earning transactions
        if ("EARNING".equals(transaction.getType()) && transaction.getDeliveryFee() > 0) {
            holder.layoutEarningDetails.setVisibility(View.VISIBLE);
            holder.tvDeliveryFee.setText("Phí giao: " + transaction.getFormattedDeliveryFee());
            holder.tvCommission.setText("Chiết khấu: " + transaction.getFormattedCommission());
            
            if (transaction.getTip() > 0) {
                holder.tvTip.setVisibility(View.VISIBLE);
                holder.tvTip.setText("Tip: " + transaction.getFormattedTip());
            } else {
                holder.tvTip.setVisibility(View.GONE);
            }
            
            // Show order ID if available
            if (transaction.getOrderId() != null) {
                holder.tvOrderId.setVisibility(View.VISIBLE);
                holder.tvOrderId.setText("Đơn hàng: #" + transaction.getOrderId());
            } else {
                holder.tvOrderId.setVisibility(View.GONE);
            }
        } else {
            holder.layoutEarningDetails.setVisibility(View.GONE);
        }
        
        // Set status indicator
        String status = transaction.getStatus();
        if ("COMPLETED".equals(status)) {
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(getStatusDisplayName(status));
            
            int statusColor;
            switch (status) {
                case "PENDING":
                    statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.warning_color);
                    break;
                case "FAILED":
                    statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.error_color);
                    break;
                default:
                    statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary);
                    break;
            }
            holder.tvStatus.setTextColor(statusColor);
        }
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING":
                return "Đang xử lý";
            case "COMPLETED":
                return "Hoàn thành";
            case "FAILED":
                return "Thất bại";
            default:
                return status;
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionType, tvDescription, tvAmount, tvDate, tvStatus;
        TextView tvDeliveryFee, tvCommission, tvTip, tvOrderId;
        View layoutEarningDetails;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            
            // Earning details
            layoutEarningDetails = itemView.findViewById(R.id.layout_earning_details);
            tvDeliveryFee = itemView.findViewById(R.id.tv_delivery_fee);
            tvCommission = itemView.findViewById(R.id.tv_commission);
            tvTip = itemView.findViewById(R.id.tv_tip);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
        }
    }
} 