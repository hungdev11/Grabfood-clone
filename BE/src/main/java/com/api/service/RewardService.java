package com.api.service;

import com.api.dto.response.RewardResponse;
import com.api.dto.response.RewardProgressResponse;

import java.util.List;

public interface RewardService {

    /**
     * Lấy danh sách phần thưởng cho shipper
     */
    List<RewardResponse> getRewardsForShipper(String shipperPhone);

    /**
     * Lấy tiến độ đạt thưởng (ngày/tuần/tháng)
     */
    RewardProgressResponse getRewardProgress(String shipperPhone);

    /**
     * Claim phần thưởng khi đã đủ điều kiện
     */
    String claimReward(Long rewardId, String shipperPhone);

    /**
     * Lấy trạng thái của một reward cụ thể cho shipper
     */
    RewardResponse getRewardStatusForShipper(Long rewardId, String shipperPhone);
}