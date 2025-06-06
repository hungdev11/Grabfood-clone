package com.grabdriver.myapplication.services;

import android.content.Context;

import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.Reward;
import com.grabdriver.myapplication.models.RewardResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class RewardRepository extends ApiRepository {

    public RewardRepository(Context context) {
        super(context);
    }

    // Lấy danh sách quà thưởng hiện có
    public void getAvailableRewards(NetworkCallback<List<Reward>> callback) {
        Call<ApiResponse<List<RewardResponse>>> call = getApiService().getAvailableRewards();
        executeCall(call, new NetworkCallback<List<RewardResponse>>() {
            @Override
            public void onSuccess(List<RewardResponse> result) {
                List<Reward> rewards = new ArrayList<>();
                if (result != null) {
                    for (RewardResponse response : result) {
                        rewards.add(response.toReward());
                    }
                }
                callback.onSuccess(rewards);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy danh sách quà thưởng đã nhận
    public void getClaimedRewards(NetworkCallback<List<Reward>> callback) {
        Call<ApiResponse<List<RewardResponse>>> call = getApiService().getClaimedRewards();
        executeCall(call, new NetworkCallback<List<RewardResponse>>() {
            @Override
            public void onSuccess(List<RewardResponse> result) {
                List<Reward> rewards = new ArrayList<>();
                if (result != null) {
                    for (RewardResponse response : result) {
                        rewards.add(response.toReward());
                    }
                }
                callback.onSuccess(rewards);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Nhận quà thưởng
    public void claimReward(long rewardId, NetworkCallback<Reward> callback) {
        Call<ApiResponse<Reward>> call = getApiService().claimReward(rewardId);
        executeCall(call, callback);
    }
    
    // Lấy tiến độ quà thưởng
    public void getRewardProgress(NetworkCallback<List<Reward>> callback) {
        Call<ApiResponse<List<RewardResponse>>> call = getApiService().getRewardProgress();
        executeCall(call, new NetworkCallback<List<RewardResponse>>() {
            @Override
            public void onSuccess(List<RewardResponse> result) {
                List<Reward> rewards = new ArrayList<>();
                if (result != null) {
                    for (RewardResponse response : result) {
                        rewards.add(response.toReward());
                    }
                }
                callback.onSuccess(rewards);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
} 