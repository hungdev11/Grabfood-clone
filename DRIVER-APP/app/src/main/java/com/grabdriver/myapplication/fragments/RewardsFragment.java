package com.grabdriver.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grabdriver.myapplication.activities.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.RewardAdapter;
import com.grabdriver.myapplication.models.Reward;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RewardsFragment extends Fragment implements RewardAdapter.OnRewardClickListener {
    private TextView totalGemsText;
    private RecyclerView rewardsRecyclerView;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressLoading;
    private TextView emptyRewardsText;

    private ApiManager apiManager;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        if (getActivity() instanceof MainActivity) {
            apiManager = ((MainActivity) getActivity()).getApiManager();
            sessionManager = ((MainActivity) getActivity()).getSessionManager();
        }

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadRewards();

        return view;
    }

    private void initViews(View view) {
        totalGemsText = view.findViewById(R.id.text_total_gems);
        rewardsRecyclerView = view.findViewById(R.id.recycler_rewards);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressLoading = view.findViewById(R.id.progress_loading);
        emptyRewardsText = view.findViewById(R.id.text_empty_rewards);
    }

    private void setupRecyclerView() {
        rewardList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(rewardList, this);
        rewardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rewardsRecyclerView.setAdapter(rewardAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark
        );
    }

    private void loadRewards() {
        showLoading(true);
        
        // Hiển thị gems từ SessionManager
        if (sessionManager != null) {
            Shipper shipper = sessionManager.getShipperInfo();
            if (shipper != null) {
                totalGemsText.setText(String.format("%,d 💎", shipper.getGems()));
            }
        }
        
        if (apiManager != null) {
            apiManager.getRewardRepository().getAvailableRewards(
                    new ApiRepository.NetworkCallback<List<Reward>>() {
                @Override
                public void onSuccess(List<Reward> result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            
                            if (result != null && !result.isEmpty()) {
                                rewardList.clear();
                                rewardList.addAll(result);
                                rewardAdapter.notifyDataSetChanged();
                                showEmptyRewards(false);
                            } else {
                                rewardList.clear();
                                rewardAdapter.notifyDataSetChanged();
                                showEmptyRewards(true);
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            showEmptyRewards(true);
                            Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
            
            // Lấy tiến độ phần thưởng
            apiManager.getRewardRepository().getRewardProgress(
                    new ApiRepository.NetworkCallback<List<Reward>>() {
                @Override
                public void onSuccess(List<Reward> result) {
                    if (getActivity() != null && result != null) {
                        getActivity().runOnUiThread(() -> {
                            // Cập nhật tiến độ cho existing rewards trong adapter
                            // Có thể implement logic merge reward progress với existing list nếu cần
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Error handled silently
                }
            });
        } else {
            showLoading(false);
            showEmptyRewards(true);
        }
    }

    private void refreshData() {
        loadRewards();
    }

    private void showLoading(boolean isLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
        if (progressLoading != null) {
            progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyRewards(boolean isEmpty) {
        if (emptyRewardsText != null) {
            emptyRewardsText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (rewardsRecyclerView != null) {
            rewardsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }
    
    @Override
    public void onRewardClick(Reward reward) {
        if (apiManager != null && reward != null) {
            // Kiểm tra trạng thái reward trước khi thực hiện claim
            if (reward.isClaimed()) {
                Toast.makeText(getContext(), "Phần thưởng này đã được nhận", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (reward.isExpired()) {
                Toast.makeText(getContext(), "Phần thưởng này đã hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!reward.isEligible()) {
                Toast.makeText(getContext(), "Bạn chưa đủ điều kiện nhận phần thưởng này", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!reward.canClaim()) {
                // Tạo thông báo chi tiết về điều kiện chưa đủ
                String message = createInsufficientConditionMessage(reward);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                return;
            }
            
            showLoading(true);
            
            apiManager.getRewardRepository().claimReward(reward.getId(), 
                    new ApiRepository.NetworkCallback<Reward>() {
                @Override
                public void onSuccess(Reward result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            
                            String successMessage = "Đã nhận phần thưởng thành công!";
                            if (result.getRewardValue() != null) {
                                successMessage += " Bạn nhận được " + 
                                    java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"))
                                        .format(result.getRewardValue());
                            }
                            if (result.getGemsValue() != null && result.getGemsValue() > 0) {
                                successMessage += " và " + result.getGemsValue() + " 💎";
                            }
                            
                            Toast.makeText(getContext(), successMessage, Toast.LENGTH_LONG).show();
                            
                            // Cập nhật lại danh sách phần thưởng
                            refreshData();
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            
                            String displayMessage = "Lỗi: " + errorMessage;
                            
                            // Xử lý các lỗi phổ biến
                            if (errorMessage.toLowerCase().contains("already claimed")) {
                                displayMessage = "Phần thưởng này đã được nhận rồi";
                            } else if (errorMessage.toLowerCase().contains("not eligible")) {
                                displayMessage = "Bạn chưa đủ điều kiện nhận phần thưởng này";
                            } else if (errorMessage.toLowerCase().contains("expired")) {
                                displayMessage = "Phần thưởng này đã hết hạn";
                            }
                            
                            Toast.makeText(getContext(), displayMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        }
    }
    
    private String createInsufficientConditionMessage(Reward reward) {
        StringBuilder message = new StringBuilder("Chưa đủ điều kiện: ");
        
        Float completionPercentage = reward.getCompletionPercentage();
        if (completionPercentage != null) {
            message.append("Tiến độ hiện tại ").append(Math.round(completionPercentage)).append("%");
        }
        
        // Thêm thông tin chi tiết về điều kiện cần thiết
        if (reward.getRequiredDeliveries() != null && reward.getRequiredDeliveries() > 0) {
            Float currentProgress = reward.getProgressValue();
            int current = currentProgress != null ? Math.round(currentProgress) : 0;
            message.append(". Cần hoàn thành ").append(reward.getRequiredDeliveries())
                   .append(" đơn hàng (hiện tại: ").append(current).append(")");
        } else if (reward.getRequiredOrders() != null && reward.getRequiredOrders() > 0) {
            Float currentProgress = reward.getProgressValue();
            int current = currentProgress != null ? Math.round(currentProgress) : 0;
            message.append(". Cần hoàn thành ").append(reward.getRequiredOrders())
                   .append(" đơn hàng (hiện tại: ").append(current).append(")");
        } else if (reward.getRequiredDistance() != null && reward.getRequiredDistance() > 0) {
            Float currentProgress = reward.getProgressValue();
            float current = currentProgress != null ? currentProgress : 0f;
            message.append(". Cần hoàn thành ").append(reward.getRequiredDistance())
                   .append(" km (hiện tại: ").append(String.format("%.1f", current)).append(" km)");
        } else if (reward.getRequiredRating() != null && reward.getRequiredRating() > 0) {
            Float currentProgress = reward.getProgressValue();
            float current = currentProgress != null ? currentProgress : 0f;
            message.append(". Cần đánh giá tối thiểu ").append(reward.getRequiredRating())
                   .append(" sao (hiện tại: ").append(String.format("%.1f", current)).append(" sao)");
        }
        
        return message.toString();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
}