package com.api.controller;

import com.api.dto.response.ApiResponse;
import com.api.dto.response.RewardResponse;
import com.api.dto.response.RewardProgressResponse;
import com.api.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {

    private final RewardService rewardService;

    /**
     * GET /api/rewards - Lấy danh sách phần thưởng
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getRewards() {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting rewards for shipper: {}", phone);

            List<RewardResponse> rewards = rewardService.getRewardsForShipper(phone);

            return ResponseEntity.ok(ApiResponse.<List<RewardResponse>>builder()
                    .code(200)
                    .message("Success")
                    .data(rewards)
                    .build());

        } catch (Exception e) {
            log.error("Error getting rewards", e);
            return ResponseEntity.status(500).body(ApiResponse.<List<RewardResponse>>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/rewards/progress - Tiến độ đạt thưởng (ngày/tuần)
     */
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<RewardProgressResponse>> getRewardProgress() {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Getting reward progress for shipper: {}", phone);

            RewardProgressResponse progress = rewardService.getRewardProgress(phone);

            return ResponseEntity.ok(ApiResponse.<RewardProgressResponse>builder()
                    .code(200)
                    .message("Success")
                    .data(progress)
                    .build());

        } catch (Exception e) {
            log.error("Error getting reward progress", e);
            return ResponseEntity.status(500).body(ApiResponse.<RewardProgressResponse>builder()
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    /**
     * POST /api/rewards/{id}/claim - Nhận thưởng sau khi hoàn thành điều kiện
     */
    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<String>> claimReward(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Shipper {} claiming reward {}", phone, id);

            String result = rewardService.claimReward(id, phone);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("Reward claimed successfully")
                    .data(result)
                    .build());

        } catch (Exception e) {
            log.error("Error claiming reward {}", id, e);
            return ResponseEntity.status(400).body(ApiResponse.<String>builder()
                    .code(400)
                    .message("Cannot claim reward: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/rewards/{id}/status - Kiểm tra trạng thái reward cụ thể
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RewardResponse>> getRewardStatus(@PathVariable Long id) {
        try {
            // Get authenticated shipper
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();

            log.debug("Checking reward {} status for shipper: {}", id, phone);

            RewardResponse reward = rewardService.getRewardStatusForShipper(id, phone);

            return ResponseEntity.ok(ApiResponse.<RewardResponse>builder()
                    .code(200)
                    .message("Success")
                    .data(reward)
                    .build());

        } catch (Exception e) {
            log.error("Error getting reward status {}", id, e);
            return ResponseEntity.status(404).body(ApiResponse.<RewardResponse>builder()
                    .code(404)
                    .message("Reward not found: " + e.getMessage())
                    .build());
        }
    }
}