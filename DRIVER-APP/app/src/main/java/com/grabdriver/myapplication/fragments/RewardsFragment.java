package com.grabdriver.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.grabdriver.myapplication.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.RewardAdapter;
import com.grabdriver.myapplication.models.Reward;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            showLoading(true);
            
            apiManager.getRewardRepository().claimReward(reward.getId(), 
                    new ApiRepository.NetworkCallback<Reward>() {
                @Override
                public void onSuccess(Reward result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), "Đã nhận phần thưởng thành công", Toast.LENGTH_SHORT).show();
                            
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
                            Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
}