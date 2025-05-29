package com.grabdriver.fe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.grabdriver.fe.R;
import com.grabdriver.fe.adapters.TransactionAdapter;
import com.grabdriver.fe.data.MockDataManager;
import com.grabdriver.fe.models.Transaction;
import com.grabdriver.fe.models.Wallet;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WalletFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    
    // Wallet Overview Views
    private TextView tvCurrentBalance, tvAccountStatus, tvCodHolding;
    private TextView tvTodayEarnings, tvWeekEarnings, tvTotalEarnings;
    private MaterialButton btnTopUp, btnDepositCod;
    private CardView cardBalanceWarning;
    
    // Tabs and Content
    private TabLayout tabLayout;
    private View layoutOverview, layoutEarnings, layoutTransactions;
    
    // Earnings Views
    private TextView tvEarningsToday, tvEarningsWeek, tvEarningsMonth;
    private TextView tvAvgPerOrder, tvTotalOrders, tvCommissionPaid;
    
    // Transactions Views
    private RecyclerView recyclerViewTransactions;
    private SwipeRefreshLayout swipeRefreshTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    
    // Data
    private Wallet wallet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        
        sharedPreferences = requireActivity().getSharedPreferences("GrabDriverPrefs", MODE_PRIVATE);
        
        initViews(view);
        setupTabs();
        loadWalletData();
        setupClickListeners();
        
        return view;
    }

    private void initViews(View view) {
        // Wallet Overview
        tvCurrentBalance = view.findViewById(R.id.tv_current_balance);
        tvAccountStatus = view.findViewById(R.id.tv_account_status);
        tvCodHolding = view.findViewById(R.id.tv_cod_holding);
        tvTodayEarnings = view.findViewById(R.id.tv_today_earnings);
        tvWeekEarnings = view.findViewById(R.id.tv_week_earnings);
        tvTotalEarnings = view.findViewById(R.id.tv_total_earnings);
        btnTopUp = view.findViewById(R.id.btn_top_up);
        btnDepositCod = view.findViewById(R.id.btn_deposit_cod);
        cardBalanceWarning = view.findViewById(R.id.card_balance_warning);
        
        // Tabs
        tabLayout = view.findViewById(R.id.tab_layout);
        layoutOverview = view.findViewById(R.id.layout_overview);
        layoutEarnings = view.findViewById(R.id.layout_earnings);
        layoutTransactions = view.findViewById(R.id.layout_transactions);
        
        // Earnings
        tvEarningsToday = view.findViewById(R.id.tv_earnings_today);
        tvEarningsWeek = view.findViewById(R.id.tv_earnings_week);
        tvEarningsMonth = view.findViewById(R.id.tv_earnings_month);
        tvAvgPerOrder = view.findViewById(R.id.tv_avg_per_order);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvCommissionPaid = view.findViewById(R.id.tv_commission_paid);
        
        // Transactions
        recyclerViewTransactions = view.findViewById(R.id.recycler_view_transactions);
        swipeRefreshTransactions = view.findViewById(R.id.swipe_refresh_transactions);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tổng quan"));
        tabLayout.addTab(tabLayout.newTab().setText("Thu nhập"));
        tabLayout.addTab(tabLayout.newTab().setText("Giao dịch"));
        
        // Show overview by default
        showTabContent(0);
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTabContent(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showTabContent(int position) {
        // Hide all layouts
        layoutOverview.setVisibility(View.GONE);
        layoutEarnings.setVisibility(View.GONE);
        layoutTransactions.setVisibility(View.GONE);
        
        // Show selected layout
        switch (position) {
            case 0: // Overview
                layoutOverview.setVisibility(View.VISIBLE);
                break;
            case 1: // Earnings
                layoutEarnings.setVisibility(View.VISIBLE);
                loadEarningsData();
                break;
            case 2: // Transactions
                layoutTransactions.setVisibility(View.VISIBLE);
                loadTransactionsData();
                break;
        }
    }

    private void loadWalletData() {
        // Create mock wallet data
        String shipperId = sharedPreferences.getString("shipperId", "SH001");
        wallet = MockDataManager.createMockWallet(shipperId);
        
        // Update overview UI
        updateOverviewUI();
    }

    private void updateOverviewUI() {
        if (wallet == null) return;
        
        tvCurrentBalance.setText(wallet.getFormattedBalance());
        tvAccountStatus.setText(wallet.getAccountStatus());
        tvCodHolding.setText(wallet.getFormattedCodHolding());
        tvTodayEarnings.setText(wallet.getFormattedTodayEarnings());
        tvWeekEarnings.setText(wallet.getFormattedWeekEarnings());
        tvTotalEarnings.setText(wallet.getFormattedTotalEarnings());
        
        // Show warning if balance is low
        if (!wallet.isEligibleForCOD()) {
            cardBalanceWarning.setVisibility(View.VISIBLE);
            btnTopUp.setText("Nạp " + String.format("%,d đ", wallet.getRequiredTopUp()));
        } else {
            cardBalanceWarning.setVisibility(View.GONE);
            btnTopUp.setText("Nạp tiền");
        }
        
        // Enable/disable COD deposit button
        btnDepositCod.setEnabled(wallet.getCodHolding() > 0);
    }

    private void loadEarningsData() {
        if (wallet == null) return;
        
        tvEarningsToday.setText(wallet.getFormattedTodayEarnings());
        tvEarningsWeek.setText(wallet.getFormattedWeekEarnings());
        tvEarningsMonth.setText(wallet.getFormattedTotalEarnings()); // Mock as month
        
        // Calculate additional stats
        int totalOrders = sharedPreferences.getInt("totalOrders", 156);
        long avgPerOrder = totalOrders > 0 ? wallet.getTotalEarnings() / totalOrders : 0;
        long commissionPaid = Math.round(wallet.getTotalEarnings() * 0.15 / 0.85); // Reverse calculate
        
        tvAvgPerOrder.setText(String.format("%,d đ", avgPerOrder));
        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvCommissionPaid.setText(String.format("%,d đ", commissionPaid));
    }

    private void loadTransactionsData() {
        if (transactionList == null) {
            String shipperId = sharedPreferences.getString("shipperId", "SH001");
            transactionList = MockDataManager.createMockTransactions(shipperId);
            setupTransactionsRecyclerView();
        }
    }

    private void setupTransactionsRecyclerView() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerViewTransactions.setAdapter(transactionAdapter);
        
        // Setup swipe refresh
        swipeRefreshTransactions.setOnRefreshListener(() -> {
            // Simulate refresh
            swipeRefreshTransactions.postDelayed(() -> {
                transactionAdapter.notifyDataSetChanged();
                swipeRefreshTransactions.setRefreshing(false);
            }, 1000);
        });
        
        swipeRefreshTransactions.setColorSchemeResources(
                R.color.primary_color,
                R.color.secondary_color,
                R.color.accent_color
        );
    }

    private void setupClickListeners() {
        btnTopUp.setOnClickListener(v -> {
            // TODO: Open top-up dialog or activity
            Toast.makeText(getContext(), "Chức năng nạp tiền đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnDepositCod.setOnClickListener(v -> {
            if (wallet.getCodHolding() > 0) {
                // TODO: Open COD deposit dialog
                Toast.makeText(getContext(), "Nộp " + wallet.getFormattedCodHolding() + " vào tài khoản", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Không có tiền COD để nộp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh wallet data when fragment becomes visible
        loadWalletData();
    }
} 