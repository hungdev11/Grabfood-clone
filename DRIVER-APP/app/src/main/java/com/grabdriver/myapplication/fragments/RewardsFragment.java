package com.grabdriver.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.adapters.RewardAdapter;
import com.grabdriver.myapplication.models.Reward;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RewardsFragment extends Fragment {

    private TextView totalGemsText;
    private RecyclerView rewardsRecyclerView;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        initViews(view);
        setupRecyclerView();
        loadRewards();

        return view;
    }

    private void initViews(View view) {
        totalGemsText = view.findViewById(R.id.text_total_gems);
        rewardsRecyclerView = view.findViewById(R.id.recycler_rewards);
    }

    private void setupRecyclerView() {
        rewardList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(rewardList);
        rewardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rewardsRecyclerView.setAdapter(rewardAdapter);
    }

    private void loadRewards() {
        // Load rewards from database/API
        // For demo purposes, using static data
        totalGemsText.setText("1,250 💎");

        rewardList.clear();

        Reward reward1 = new Reward(1, "Hoàn thành 10 đơn hàng", "Giao thành công 10 đơn hàng trong ngày",
                "DAILY", new BigDecimal("50000"), "ACTIVE", "", new Date());
        reward1.setRequiredOrders(10);
        reward1.setGemsValue(100);

        Reward reward2 = new Reward(2, "Giờ cao điểm", "Thưởng thêm cho các đơn hàng trong giờ cao điểm",
                "PEAK_HOUR", new BigDecimal("15000"), "ACTIVE", "", new Date());
        reward2.setPeakStartTime("11:00");
        reward2.setPeakEndTime("13:00");
        reward2.setGemsValue(50);

        Reward reward3 = new Reward(3, "Đánh giá 5 sao", "Nhận được đánh giá 5 sao từ khách hàng",
                "ACHIEVEMENT", new BigDecimal("10000"), "ACTIVE", "", new Date());
        reward3.setRequiredRating(5.0f);
        reward3.setGemsValue(25);

        Reward reward4 = new Reward(4, "Thưởng cuối tuần", "Hoàn thành ít nhất 50 đơn trong tuần",
                "BONUS", new BigDecimal("100000"), "EXPIRED", "", new Date());
        reward4.setRequiredOrders(50);
        reward4.setGemsValue(200);

        rewardList.add(reward1);
        rewardList.add(reward2);
        rewardList.add(reward3);
        rewardList.add(reward4);

        rewardAdapter.notifyDataSetChanged();
    }
}