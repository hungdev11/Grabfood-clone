package com.grabdriver.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Reward;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> rewardList;

    public RewardAdapter(List<Reward> rewardList) {
        this.rewardList = rewardList;
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
        private TextView typeText;
        private TextView rewardValueText;
        private TextView gemsValueText;
        private TextView statusText;
        private TextView requirementText;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            descriptionText = itemView.findViewById(R.id.text_description);
            typeText = itemView.findViewById(R.id.text_type);
            rewardValueText = itemView.findViewById(R.id.text_reward_value);
            gemsValueText = itemView.findViewById(R.id.text_gems_value);
            statusText = itemView.findViewById(R.id.text_status);
            requirementText = itemView.findViewById(R.id.text_requirement);
        }

        public void bind(Reward reward) {
            titleText.setText(reward.getTitle());
            descriptionText.setText(reward.getDescription());

            if (reward.getRewardValue() != null) {
                rewardValueText.setText(formatCurrency(reward.getRewardValue().longValue()));
            }

            if (reward.getGemsValue() != null) {
                gemsValueText.setText(reward.getGemsValue() + " 💎");
            }

            // Set type
            setTypeText(reward.getType());

            // Set status
            setStatusText(reward.getStatus());

            // Set requirement
            setRequirementText(reward);
        }

        private void setTypeText(String type) {
            switch (type) {
                case "DAILY":
                    typeText.setText("Hàng ngày");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.grab_green));
                    break;
                case "PEAK_HOUR":
                    typeText.setText("Giờ cao điểm");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                    break;
                case "BONUS":
                    typeText.setText("Thưởng");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.earnings_positive));
                    break;
                case "ACHIEVEMENT":
                    typeText.setText("Thành tích");
                    typeText.setTextColor(itemView.getContext().getColor(R.color.grab_green));
                    break;
                default:
                    typeText.setText(type);
                    typeText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private void setStatusText(String status) {
            switch (status) {
                case "ACTIVE":
                    statusText.setText("Đang hoạt động");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_online));
                    break;
                case "EXPIRED":
                    statusText.setText("Đã hết hạn");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.status_offline));
                    break;
                case "INACTIVE":
                    statusText.setText("Không hoạt động");
                    statusText.setTextColor(itemView.getContext().getColor(R.color.text_hint));
                    break;
                default:
                    statusText.setText(status);
                    statusText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private void setRequirementText(Reward reward) {
            StringBuilder requirement = new StringBuilder();

            if (reward.getRequiredOrders() != null) {
                requirement.append("Cần ").append(reward.getRequiredOrders()).append(" đơn hàng");
            }

            if (reward.getRequiredRating() != null) {
                if (requirement.length() > 0)
                    requirement.append(" • ");
                requirement.append("Đánh giá ").append(reward.getRequiredRating()).append(" sao");
            }

            if (reward.getPeakStartTime() != null && reward.getPeakEndTime() != null) {
                if (requirement.length() > 0)
                    requirement.append(" • ");
                requirement.append("Từ ").append(reward.getPeakStartTime())
                        .append(" đến ").append(reward.getPeakEndTime());
            }

            if (requirement.length() > 0) {
                requirementText.setText(requirement.toString());
                requirementText.setVisibility(View.VISIBLE);
            } else {
                requirementText.setVisibility(View.GONE);
            }
        }

        private String formatCurrency(long amount) {
            return String.format("%,d₫", amount);
        }
    }
}