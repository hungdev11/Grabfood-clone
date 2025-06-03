package com.grabdriver.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Transaction;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private SimpleDateFormat dateFormat;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        this.dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionText;
        private TextView amountText;
        private TextView typeText;
        private TextView dateText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.text_description);
            amountText = itemView.findViewById(R.id.text_amount);
            typeText = itemView.findViewById(R.id.text_type);
            dateText = itemView.findViewById(R.id.text_date);
        }

        public void bind(Transaction transaction) {
            descriptionText.setText(transaction.getDescription());
            dateText.setText(dateFormat.format(transaction.getTransactionDate()));

            // Format amount with sign
            long amount = transaction.getAmount();
            String amountText = formatCurrency(Math.abs(amount));
            if (amount >= 0) {
                this.amountText.setText("+" + amountText);
                this.amountText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
            } else {
                this.amountText.setText("-" + amountText);
                this.amountText.setTextColor(itemView.getContext().getColor(R.color.earnings_negative));
            }

            // Set type text and color
            setTypeText(transaction.getType());
        }

        private void setTypeText(String type) {
            switch (type) {
                case "EARNING":
                    typeText.setText("Thu nhập");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
                    break;
                case "TIP":
                    typeText.setText("Tiền tip");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
                    break;
                case "BONUS":
                    typeText.setText("Thưởng");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
                    break;
                case "COD_DEPOSIT":
                    typeText.setText("Nộp COD");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_negative));
                    break;
                case "COMMISSION":
                    typeText.setText("Hoa hồng");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_negative));
                    break;
                case "TOP_UP":
                    typeText.setText("Nạp tiền");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
                    break;
                default:
                    typeText.setText(type);
                    typeText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private String formatCurrency(long amount) {
            return String.format("%,d₫", amount);
        }
    }
}