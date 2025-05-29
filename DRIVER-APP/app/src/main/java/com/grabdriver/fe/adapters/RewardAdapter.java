package com.grabdriver.fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grabdriver.fe.R;
import com.grabdriver.fe.models.Reward;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> rewardList;
    private SimpleDateFormat dateFormat;

    public RewardAdapter(List<Reward> rewardList) {
        this.rewardList = rewardList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        
        // Use title if available, otherwise use description
        String title = reward.getTitle() != null ? reward.getTitle() : reward.getRewardDescription();
        holder.tvRewardType.setText(title);
        
        holder.tvRewardAmount.setText(reward.getFormattedRewardAmount());
        
        // Show date based on completion status
        if (reward.getIsCompleted() && reward.getCompletedDate() != null) {
            holder.tvDateEarned.setText("Hoàn thành: " + dateFormat.format(reward.getCompletedDate()));
        } else if (reward.getExpiryDate() != null) {
            holder.tvDateEarned.setText("Hết hạn: " + dateFormat.format(reward.getExpiryDate()));
        } else if (reward.getDateEarned() != null) {
            holder.tvDateEarned.setText(dateFormat.format(reward.getDateEarned()));
        } else {
            holder.tvDateEarned.setText("");
        }
        
        holder.tvGemsEarned.setText(reward.getGemsRequired() + " ngọc");
        
        // Show completion status
        if (reward.getIsCompleted()) {
            holder.tvStatus.setText("Đã hoàn thành");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success_color));
        } else {
            holder.tvStatus.setText("Đang thực hiện");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.warning_color));
        }
        
        // Show progress if available
        if (reward.getTargetProgress() > 0) {
            holder.tvPerformance.setText(String.format(Locale.getDefault(),
                    "Tiến độ: %d/%d (%d%%)",
                    reward.getCurrentProgress(),
                    reward.getTargetProgress(),
                    (int) ((float) reward.getCurrentProgress() / reward.getTargetProgress() * 100)));
        } else {
            // Show performance stats if available
            if (reward.getAcceptanceRate() > 0) {
                holder.tvPerformance.setText(String.format(Locale.getDefault(),
                        "Nhận: %.0f%% | Hủy: %.0f%% | Đánh giá: %.1f⭐",
                        reward.getAcceptanceRate(),
                        reward.getCancellationRate(),
                        reward.getAverageRating()));
            } else {
                holder.tvPerformance.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRewardType, tvRewardAmount, tvDateEarned, tvGemsEarned, tvStatus, tvPerformance;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRewardType = itemView.findViewById(R.id.tv_reward_type);
            tvRewardAmount = itemView.findViewById(R.id.tv_reward_amount);
            tvDateEarned = itemView.findViewById(R.id.tv_date_earned);
            tvGemsEarned = itemView.findViewById(R.id.tv_gems_earned);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPerformance = itemView.findViewById(R.id.tv_performance);
        }
    }
} 