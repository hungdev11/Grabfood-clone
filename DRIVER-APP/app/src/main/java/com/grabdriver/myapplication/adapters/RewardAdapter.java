package com.grabdriver.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Reward;
import com.grabdriver.myapplication.utils.DateTimeUtil;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> rewardList;
    private OnRewardClickListener listener;

    public interface OnRewardClickListener {
        void onRewardClick(Reward reward);
    }

    public RewardAdapter(List<Reward> rewardList) {
        this.rewardList = rewardList;
    }
    
    public RewardAdapter(List<Reward> rewardList, OnRewardClickListener listener) {
        this.rewardList = rewardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        holder.bind(reward);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private TextView valueText;
        private TextView gemsText;
        private TextView statusText;
        private Button claimButton;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_reward_title);
            descriptionText = itemView.findViewById(R.id.text_reward_description);
            valueText = itemView.findViewById(R.id.text_reward_value);
            gemsText = itemView.findViewById(R.id.text_reward_gems);
            statusText = itemView.findViewById(R.id.text_reward_status);
            claimButton = itemView.findViewById(R.id.btn_claim_reward);
        }

        void bind(Reward reward) {
            titleText.setText(reward.getTitle());
            descriptionText.setText(reward.getDescription());
            
            // Format currency
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedValue = currencyFormat.format(reward.getValue());
            valueText.setText(formattedValue);
            
            // Display gems if available
            if (reward.getGemsValue() > 0) {
                gemsText.setText(reward.getGemsValue() + " 💎");
                gemsText.setVisibility(View.VISIBLE);
            } else {
                gemsText.setVisibility(View.GONE);
            }

            // Set status
            statusText.setText(getStatusText(reward.getStatus()));
            
            // Configure claim button
            boolean isActive = "ACTIVE".equals(reward.getStatus());
            claimButton.setEnabled(isActive);
            claimButton.setText(isActive ? "Nhận thưởng" : "Đã nhận");
            
            claimButton.setOnClickListener(v -> {
                if (listener != null && isActive) {
                    listener.onRewardClick(reward);
                }
            });
        }

        private String getStatusText(String status) {
            switch (status) {
                case "ACTIVE":
                    return "Khả dụng";
                case "CLAIMED":
                    return "Đã nhận";
                case "EXPIRED":
                    return "Hết hạn";
                default:
                    return status;
            }
        }
    }
}