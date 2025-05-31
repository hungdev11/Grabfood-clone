package com.api.service.Imp;

import com.api.dto.response.RewardResponse;
import com.api.dto.response.RewardProgressResponse;
import com.api.entity.*;
import com.api.repository.*;
import com.api.service.RewardService;
import com.api.service.ShipperService;
import com.api.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImp implements RewardService {

    private final RewardRepository rewardRepository;
    private final ShipperRewardRepository shipperRewardRepository;
    private final ShipperService shipperService;
    private final OrderRepository orderRepository;

    @Override
    public List<RewardResponse> getRewardsForShipper(String shipperPhone) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Get all valid rewards
            List<Reward> validRewards = rewardRepository.findValidRewards(LocalDateTime.now());

            // Convert to response with progress info
            return validRewards.stream()
                    .map(reward -> buildRewardResponse(reward, shipper))
                    .toList();

        } catch (Exception e) {
            log.error("Error getting rewards for shipper {}", shipperPhone, e);
            throw new RuntimeException("Failed to get rewards: " + e.getMessage());
        }
    }

    @Override
    public RewardProgressResponse getRewardProgress(String shipperPhone) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Get today's date range
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

            // Get week range
            LocalDateTime startOfWeek = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).atStartOfDay();

            // Get month range
            LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

            // Get all orders for calculation
            List<Order> allOrders = orderRepository.findAllByShipperId(shipper.getId());

            // Filter orders by time periods
            List<Order> todayOrders = filterOrdersByDateRange(allOrders, startOfDay, endOfDay);
            List<Order> weekOrders = filterOrdersByDateRange(allOrders, startOfWeek, LocalDateTime.now());
            List<Order> monthOrders = filterOrdersByDateRange(allOrders, startOfMonth, LocalDateTime.now());

            // Calculate progress stats
            RewardProgressResponse.DailyProgress dailyProgress = calculateDailyProgress(todayOrders, shipper);
            RewardProgressResponse.WeeklyProgress weeklyProgress = calculateWeeklyProgress(weekOrders, shipper);
            RewardProgressResponse.MonthlyProgress monthlyProgress = calculateMonthlyProgress(monthOrders, shipper);

            // Get reward counts
            Integer totalRewards = rewardRepository.findValidRewards(LocalDateTime.now()).size();
            Integer claimedRewards = shipperRewardRepository.countClaimedRewards(shipper.getId());
            Integer eligibleRewards = shipperRewardRepository.countEligibleRewards(shipper.getId());

            // Get available rewards
            List<RewardResponse> availableRewards = getRewardsForShipper(shipperPhone);

            return RewardProgressResponse.builder()
                    .totalRewards(totalRewards)
                    .claimedRewards(claimedRewards != null ? claimedRewards : 0)
                    .eligibleRewards(eligibleRewards != null ? eligibleRewards : 0)
                    .todayProgress(dailyProgress)
                    .weeklyProgress(weeklyProgress)
                    .monthlyProgress(monthlyProgress)
                    .availableRewards(availableRewards)
                    .build();

        } catch (Exception e) {
            log.error("Error getting reward progress for shipper {}", shipperPhone, e);
            throw new RuntimeException("Failed to get reward progress: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String claimReward(Long rewardId, String shipperPhone) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Get reward
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new RuntimeException("Reward not found"));

            // Check if reward is valid
            if (reward.getStatus() != RewardStatus.ACTIVE) {
                throw new RuntimeException("Reward is not active");
            }

            if (reward.getEndDate() != null && reward.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Reward has expired");
            }

            // Check if already claimed
            Optional<ShipperReward> existingClaim = shipperRewardRepository
                    .findByShipperIdAndRewardId(shipper.getId(), rewardId);

            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.CLAIMED) {
                throw new RuntimeException("Reward already claimed");
            }

            // Check if shipper meets requirements
            if (!checkRewardEligibility(reward, shipper)) {
                throw new RuntimeException("Shipper does not meet reward requirements");
            }

            // Create or update shipper reward
            ShipperReward shipperReward = existingClaim.orElse(ShipperReward.builder()
                    .shipper(shipper)
                    .reward(reward)
                    .build());

            shipperReward.setStatus(ClaimStatus.CLAIMED);
            shipperReward.setClaimedAt(LocalDateTime.now());
            shipperReward.setCompletionPercentage(100.0f);

            shipperRewardRepository.save(shipperReward);

            // Apply reward benefits to shipper
            applyRewardBenefits(reward, shipper);

            log.info("Shipper {} successfully claimed reward {}", shipperPhone, rewardId);

            return String.format("Successfully claimed reward: %s. " +
                    "Received: %s gems, %s VND",
                    reward.getTitle(),
                    reward.getGemsValue() != null ? reward.getGemsValue() : 0,
                    reward.getRewardValue() != null ? reward.getRewardValue() : BigDecimal.ZERO);

        } catch (Exception e) {
            log.error("Error claiming reward {} for shipper {}", rewardId, shipperPhone, e);
            throw new RuntimeException("Failed to claim reward: " + e.getMessage());
        }
    }

    @Override
    public RewardResponse getRewardStatusForShipper(Long rewardId, String shipperPhone) {
        try {
            // Get shipper
            Shipper shipper = shipperService.getShipperByPhone(shipperPhone);

            // Get reward
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new RuntimeException("Reward not found"));

            // Build response using existing helper method
            return buildRewardResponse(reward, shipper);

        } catch (Exception e) {
            log.error("Error getting reward status {} for shipper {}", rewardId, shipperPhone, e);
            throw new RuntimeException("Failed to get reward status: " + e.getMessage());
        }
    }

    // ===== HELPER METHODS =====

    private RewardResponse buildRewardResponse(Reward reward, Shipper shipper) {
        // Check existing claim
        Optional<ShipperReward> existingClaim = shipperRewardRepository
                .findByShipperIdAndRewardId(shipper.getId(), reward.getId());

        ClaimStatus claimStatus = existingClaim.map(ShipperReward::getStatus)
                .orElse(ClaimStatus.ELIGIBLE);

        // Calculate progress
        ProgressCalculation progress = calculateRewardProgress(reward, shipper);

        return RewardResponse.builder()
                .id(reward.getId())
                .title(reward.getTitle())
                .description(reward.getDescription())
                .type(reward.getType())
                .rewardValue(reward.getRewardValue())
                .gemsValue(reward.getGemsValue())
                .requiredOrders(reward.getRequiredOrders())
                .requiredDistance(reward.getRequiredDistance())
                .requiredRating(reward.getRequiredRating())
                .startDate(reward.getStartDate())
                .endDate(reward.getEndDate())
                .claimStatus(claimStatus)
                .progressValue(progress.currentValue)
                .completionPercentage(progress.percentage)
                .isClaimable(progress.isClaimable)
                .iconUrl(reward.getIconUrl())
                .build();
    }

    private ProgressCalculation calculateRewardProgress(Reward reward, Shipper shipper) {
        LocalDate today = LocalDate.now();
        LocalDateTime startDate = getStartDateForRewardType(reward.getType(), today);
        LocalDateTime endDate = LocalDateTime.now();

        List<Order> relevantOrders = orderRepository.findAllByShipperId(shipper.getId())
                .stream()
                .filter(order -> order.getOrderDate().isAfter(startDate) &&
                        order.getOrderDate().isBefore(endDate) &&
                        order.getStatus() == OrderStatus.COMPLETED)
                .toList();

        float currentValue = 0;
        float targetValue = 1;
        boolean isClaimable = false;

        if (reward.getRequiredOrders() != null) {
            currentValue = relevantOrders.size();
            targetValue = reward.getRequiredOrders();
            isClaimable = currentValue >= targetValue;
        } else if (reward.getRequiredDistance() != null) {
            currentValue = (float) relevantOrders.stream()
                    .mapToDouble(order -> order.getDistance() != null ? order.getDistance() / 1000.0 : 0.0)
                    .sum();
            targetValue = reward.getRequiredDistance();
            isClaimable = currentValue >= targetValue;
        } else if (reward.getRequiredRating() != null) {
            currentValue = shipper.getRating() != null ? shipper.getRating().floatValue() : 0.0f;
            targetValue = reward.getRequiredRating();
            isClaimable = currentValue >= targetValue;
        }

        float percentage = Math.min((currentValue / targetValue) * 100, 100);

        return new ProgressCalculation(currentValue, percentage, isClaimable);
    }

    private LocalDateTime getStartDateForRewardType(RewardType type, LocalDate today) {
        return switch (type) {
            case DAILY -> today.atStartOfDay();
            case WEEKLY -> today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).atStartOfDay();
            case MONTHLY -> today.withDayOfMonth(1).atStartOfDay();
            case ACHIEVEMENT -> LocalDateTime.of(2020, 1, 1, 0, 0); // All time
            case PEAK_HOUR -> today.atStartOfDay(); // Same as daily for peak hour rewards
            case DISTANCE -> today.withDayOfMonth(1).atStartOfDay(); // Monthly distance tracking
            case RATING -> LocalDateTime.of(2020, 1, 1, 0, 0); // All time for rating
            case BONUS -> today.atStartOfDay(); // Daily for bonus rewards
        };
    }

    private List<Order> filterOrdersByDateRange(List<Order> orders, LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(order -> order.getOrderDate().isAfter(start) &&
                        order.getOrderDate().isBefore(end) &&
                        order.getStatus() == OrderStatus.COMPLETED)
                .toList();
    }

    private RewardProgressResponse.DailyProgress calculateDailyProgress(List<Order> todayOrders, Shipper shipper) {
        int ordersCompleted = todayOrders.size();
        int ordersTarget = 10; // Default daily target

        float distanceTraveled = (float) todayOrders.stream()
                .mapToDouble(order -> order.getDistance() != null ? order.getDistance() / 1000.0 : 0.0)
                .sum();
        float distanceTarget = 50.0f; // 50km daily target

        float currentRating = shipper.getRating() != null ? shipper.getRating().floatValue() : 0.0f;

        // Check if daily reward claimed
        boolean dailyRewardClaimed = shipperRewardRepository.findByShipperId(shipper.getId())
                .stream()
                .anyMatch(sr -> sr.getReward().getType() == RewardType.DAILY &&
                        sr.getStatus() == ClaimStatus.CLAIMED &&
                        sr.getClaimedAt() != null &&
                        sr.getClaimedAt().toLocalDate().equals(LocalDate.now()));

        return RewardProgressResponse.DailyProgress.builder()
                .ordersCompleted(ordersCompleted)
                .ordersTarget(ordersTarget)
                .distanceTraveled(distanceTraveled)
                .distanceTarget(distanceTarget)
                .currentRating(currentRating)
                .dailyRewardClaimed(dailyRewardClaimed)
                .build();
    }

    private RewardProgressResponse.WeeklyProgress calculateWeeklyProgress(List<Order> weekOrders, Shipper shipper) {
        int ordersCompleted = weekOrders.size();
        int ordersTarget = 50; // Weekly target

        float distanceTraveled = (float) weekOrders.stream()
                .mapToDouble(order -> order.getDistance() != null ? order.getDistance() / 1000.0 : 0.0)
                .sum();
        float distanceTarget = 300.0f; // 300km weekly target

        // Check if weekly reward claimed
        boolean weeklyRewardClaimed = shipperRewardRepository.findByShipperId(shipper.getId())
                .stream()
                .anyMatch(sr -> sr.getReward().getType() == RewardType.WEEKLY &&
                        sr.getStatus() == ClaimStatus.CLAIMED &&
                        sr.getClaimedAt() != null &&
                        sr.getClaimedAt().toLocalDate().equals(LocalDate.now()));

        return RewardProgressResponse.WeeklyProgress.builder()
                .ordersCompleted(ordersCompleted)
                .ordersTarget(ordersTarget)
                .distanceTraveled(distanceTraveled)
                .distanceTarget(distanceTarget)
                .weeklyRewardClaimed(weeklyRewardClaimed)
                .build();
    }

    private RewardProgressResponse.MonthlyProgress calculateMonthlyProgress(List<Order> monthOrders, Shipper shipper) {
        int ordersCompleted = monthOrders.size();
        int ordersTarget = 200; // Monthly target

        float distanceTraveled = (float) monthOrders.stream()
                .mapToDouble(order -> order.getDistance() != null ? order.getDistance() / 1000.0 : 0.0)
                .sum();
        float distanceTarget = 1000.0f; // 1000km monthly target

        // Check if monthly reward claimed
        boolean monthlyRewardClaimed = shipperRewardRepository.findByShipperId(shipper.getId())
                .stream()
                .anyMatch(sr -> sr.getReward().getType() == RewardType.MONTHLY &&
                        sr.getStatus() == ClaimStatus.CLAIMED &&
                        sr.getClaimedAt() != null &&
                        sr.getClaimedAt().getMonth().equals(LocalDate.now().getMonth()));

        return RewardProgressResponse.MonthlyProgress.builder()
                .ordersCompleted(ordersCompleted)
                .ordersTarget(ordersTarget)
                .distanceTraveled(distanceTraveled)
                .distanceTarget(distanceTarget)
                .monthlyRewardClaimed(monthlyRewardClaimed)
                .build();
    }

    private boolean checkRewardEligibility(Reward reward, Shipper shipper) {
        ProgressCalculation progress = calculateRewardProgress(reward, shipper);
        return progress.isClaimable;
    }

    private void applyRewardBenefits(Reward reward, Shipper shipper) {
        // This would typically update shipper's balance/gems
        // For now, just log the benefits
        log.info("Applied reward benefits to shipper {}: {} gems, {} VND",
                shipper.getPhone(),
                reward.getGemsValue() != null ? reward.getGemsValue() : 0,
                reward.getRewardValue() != null ? reward.getRewardValue() : BigDecimal.ZERO);
    }

    // Inner class for calculation results
    private record ProgressCalculation(float currentValue, float percentage, boolean isClaimable) {
    }
}