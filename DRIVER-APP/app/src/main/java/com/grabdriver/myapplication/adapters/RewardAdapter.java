package com.grabdriver.myapplication.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        private LinearLayout progressSection;
        private TextView progressPercentageText;
        private ProgressBar progressBar;
        private TextView progressDetailText;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_reward_title);
            descriptionText = itemView.findViewById(R.id.text_reward_description);
            valueText = itemView.findViewById(R.id.text_reward_value);
            gemsText = itemView.findViewById(R.id.text_reward_gems);
            statusText = itemView.findViewById(R.id.text_reward_status);
            claimButton = itemView.findViewById(R.id.btn_claim_reward);
            progressSection = itemView.findViewById(R.id.layout_progress_section);
            progressPercentageText = itemView.findViewById(R.id.text_progress_percentage);
            progressBar = itemView.findViewById(R.id.progress_reward);
            progressDetailText = itemView.findViewById(R.id.text_progress_detail);
        }

        void bind(Reward reward) {
            titleText.setText(reward.getTitle());
            descriptionText.setText(reward.getDescription());
            
            // Format currency
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedValue = currencyFormat.format(reward.getValue());
            valueText.setText(formattedValue);
            
            // Display gems if available
            if (reward.getGemsValue() != null && reward.getGemsValue() > 0) {
                gemsText.setText(reward.getGemsValue() + " 💎");
                gemsText.setVisibility(View.VISIBLE);
            } else {
                gemsText.setVisibility(View.GONE);
            }

            // Configure based on shipper reward status
            configureByShipperRewardStatus(reward);
        }

        private void configureByShipperRewardStatus(Reward reward) {
            String shipperStatus = reward.getShipperRewardStatus();
            
            if (shipperStatus == null) {
                // Default to ELIGIBLE if no status
                shipperStatus = "ELIGIBLE";
            }

            switch (shipperStatus) {
                case "CLAIMED":
                    configureClaimed(reward);
                    break;
                case "EXPIRED":
                    configureExpired(reward);
                    break;
                case "ELIGIBLE":
                default:
                    configureEligible(reward);
                    break;
            }
        }

        private void configureClaimed(Reward reward) {
            // Status
            statusText.setText("Đã nhận");
            statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed));
            
            // Progress section - hidden for claimed rewards
            progressSection.setVisibility(View.GONE);
            
            // Button
            claimButton.setText("Đã nhận");
            claimButton.setEnabled(false);
            claimButton.setAlpha(0.6f);
            claimButton.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.text_secondary));
            claimButton.setOnClickListener(null);
        }

        private void configureExpired(Reward reward) {
            // Status
            statusText.setText("Hết hạn");
            statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_rejected));
            
            // Progress section - hidden for expired rewards
            progressSection.setVisibility(View.GONE);
            
            // Button
            claimButton.setText("Hết hạn");
            claimButton.setEnabled(false);
            claimButton.setAlpha(0.6f);
            claimButton.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.status_rejected));
            claimButton.setOnClickListener(null);
        }

        private void configureEligible(Reward reward) {
            // Status
            statusText.setText("Khả dụng");
            statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_online));
            
            // Show progress if available
            if (reward.getCompletionPercentage() != null) {
                progressSection.setVisibility(View.VISIBLE);
                
                int percentage = Math.round(reward.getCompletionPercentage());
                progressPercentageText.setText(percentage + "%");
                progressBar.setProgress(percentage);
                
                // Create progress detail text
                String progressDetail = createProgressDetailText(reward);
                progressDetailText.setText(progressDetail);
                
                // Configure button based on completion
                if (reward.canClaim()) {
                    configureCanClaimButton(reward);
                } else {
                    configureCannotClaimButton(reward);
                }
            } else {
                progressSection.setVisibility(View.GONE);
                configureCanClaimButton(reward); // Assume can claim if no progress info
            }
        }

        private void configureCanClaimButton(Reward reward) {
            claimButton.setText("Nhận");
            claimButton.setEnabled(true);
            claimButton.setAlpha(1.0f);
            claimButton.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.grab_green));
            claimButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRewardClick(reward);
                }
            });
        }

        private void configureCannotClaimButton(Reward reward) {
            claimButton.setText("Chưa đủ điều kiện");
            claimButton.setEnabled(false);
            claimButton.setAlpha(0.6f);
            claimButton.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.text_secondary));
            claimButton.setOnClickListener(null);
        }

        private String createProgressDetailText(Reward reward) {
            Float progressValue = reward.getProgressValue();
            
            if (progressValue == null) {
                return "Tiến độ không khả dụng";
            }

            // Create appropriate progress text based on reward requirements
            if (reward.getRequiredDeliveries() != null && reward.getRequiredDeliveries() > 0) {
                int current = Math.round(progressValue);
                int required = reward.getRequiredDeliveries();
                return current + "/" + required + " đơn hàng đã hoàn thành";
            } else if (reward.getRequiredOrders() != null && reward.getRequiredOrders() > 0) {
                int current = Math.round(progressValue);
                int required = reward.getRequiredOrders();
                return current + "/" + required + " đơn hàng đã hoàn thành";
            } else if (reward.getRequiredDistance() != null && reward.getRequiredDistance() > 0) {
                return String.format("%.1f/%.1f km đã hoàn thành", progressValue, reward.getRequiredDistance());
            } else if (reward.getRequiredRating() != null && reward.getRequiredRating() > 0) {
                return String.format("Đánh giá hiện tại: %.1f/%.1f sao", progressValue, reward.getRequiredRating());
            }
            
            return "Tiến độ: " + String.format("%.1f", progressValue);
        }
    }
}