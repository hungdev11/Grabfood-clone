package com.grabdriver.fe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grabdriver.fe.R;
import com.grabdriver.fe.adapters.RewardAdapter;
import com.grabdriver.fe.data.MockDataManager;
import com.grabdriver.fe.models.Reward;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RewardsFragment extends Fragment {

    private TextView tvTotalGems, tvDailyProgress, tvWeeklyProgress;
    private ProgressBar progressDaily, progressWeekly;
    private RecyclerView recyclerViewRewards;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        
        sharedPreferences = requireActivity().getSharedPreferences("GrabDriverPrefs", MODE_PRIVATE);
        
        initViews(view);
        setupRecyclerView();
        loadRewardsData();
        
        return view;
    }

    private void initViews(View view) {
        tvTotalGems = view.findViewById(R.id.tv_current_gems);
        tvDailyProgress = view.findViewById(R.id.tv_daily_progress);
        tvWeeklyProgress = view.findViewById(R.id.tv_weekly_progress);
        progressDaily = view.findViewById(R.id.progress_daily);
        progressWeekly = view.findViewById(R.id.progress_weekly);
        recyclerViewRewards = view.findViewById(R.id.recycler_view_rewards);
    }

    private void setupRecyclerView() {
        recyclerViewRewards.setLayoutManager(new LinearLayoutManager(getContext()));
        rewardList = MockDataManager.createMockRewards();
        rewardAdapter = new RewardAdapter(rewardList);
        recyclerViewRewards.setAdapter(rewardAdapter);
    }

    private void loadRewardsData() {
        // Load gems from SharedPreferences
        int totalGems = sharedPreferences.getInt("gems", 0);
        tvTotalGems.setText(String.valueOf(totalGems));

        // Load rewards and calculate progress
        rewardList.clear();
        rewardList.addAll(MockDataManager.createMockRewards());
        
        calculateProgress();
        
        if (rewardAdapter != null) {
            rewardAdapter.notifyDataSetChanged();
        }
    }

    private void calculateProgress() {
        // Find daily and weekly rewards to show progress
        Reward dailyReward = null;
        Reward weeklyReward = null;
        
        for (Reward reward : rewardList) {
            if ("daily".equals(reward.getType()) && !reward.getIsCompleted()) {
                if (dailyReward == null || reward.getTargetProgress() < dailyReward.getTargetProgress()) {
                    dailyReward = reward;
                }
            } else if ("weekly".equals(reward.getType()) && !reward.getIsCompleted()) {
                if (weeklyReward == null || reward.getTargetProgress() < weeklyReward.getTargetProgress()) {
                    weeklyReward = reward;
                }
            }
        }

        // Update daily progress
        if (dailyReward != null) {
            int dailyProgressValue = (int) ((float) dailyReward.getCurrentProgress() / dailyReward.getTargetProgress() * 100);
            progressDaily.setProgress(dailyProgressValue);
            tvDailyProgress.setText(dailyReward.getCurrentProgress() + "/" + dailyReward.getTargetProgress() + " đơn hàng");
        } else {
            progressDaily.setProgress(100);
            tvDailyProgress.setText("Hoàn thành!");
        }

        // Update weekly progress
        if (weeklyReward != null) {
            int weeklyProgressValue = (int) ((float) weeklyReward.getCurrentProgress() / weeklyReward.getTargetProgress() * 100);
            progressWeekly.setProgress(weeklyProgressValue);
            tvWeeklyProgress.setText(weeklyReward.getCurrentProgress() + "/" + weeklyReward.getTargetProgress() + " đơn hàng");
        } else {
            progressWeekly.setProgress(100);
            tvWeeklyProgress.setText("Hoàn thành!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh rewards when fragment becomes visible
        if (sharedPreferences != null) {
            loadRewardsData();
        }
    }
} 