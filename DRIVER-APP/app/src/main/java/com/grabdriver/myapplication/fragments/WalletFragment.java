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
import com.grabdriver.myapplication.adapters.TransactionAdapter;
import com.grabdriver.myapplication.models.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalletFragment extends Fragment {

    private TextView currentBalanceText;
    private TextView todayEarningsText;
    private TextView weekEarningsText;
    private TextView monthEarningsText;
    private TextView codHoldingText;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        initViews(view);
        setupRecyclerView();
        loadWalletData();
        loadTransactions();

        return view;
    }

    private void initViews(View view) {
        currentBalanceText = view.findViewById(R.id.text_current_balance);
        todayEarningsText = view.findViewById(R.id.text_today_earnings);
        weekEarningsText = view.findViewById(R.id.text_week_earnings);
        monthEarningsText = view.findViewById(R.id.text_month_earnings);
        codHoldingText = view.findViewById(R.id.text_cod_holding);
        transactionsRecyclerView = view.findViewById(R.id.recycler_transactions);
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void loadWalletData() {
        // Load wallet data from database/API
        // For demo purposes, using static data
        currentBalanceText.setText(formatCurrency(850000));
        todayEarningsText.setText(formatCurrency(125000));
        weekEarningsText.setText(formatCurrency(680000));
        monthEarningsText.setText(formatCurrency(2450000));
        codHoldingText.setText(formatCurrency(150000));
    }

    private void loadTransactions() {
        // Load transactions from database/API
        // For demo purposes, using static data
        transactionList.clear();

        Transaction t1 = new Transaction(1, 25000, "EARNING", "COMPLETED", new Date(), "Phí giao hàng đơn #123");
        t1.setOrderId(123L);

        Transaction t2 = new Transaction(2, 5000, "TIP", "COMPLETED", new Date(), "Tiền tip từ khách hàng");
        t2.setOrderId(122L);

        Transaction t3 = new Transaction(3, 20000, "EARNING", "COMPLETED", new Date(), "Phí giao hàng đơn #121");
        t3.setOrderId(121L);

        Transaction t4 = new Transaction(4, -50000, "COD_DEPOSIT", "COMPLETED", new Date(), "Nộp tiền COD");

        Transaction t5 = new Transaction(5, 10000, "BONUS", "COMPLETED", new Date(), "Thưởng hoàn thành 10 đơn");

        transactionList.add(t1);
        transactionList.add(t2);
        transactionList.add(t3);
        transactionList.add(t4);
        transactionList.add(t5);

        transactionAdapter.notifyDataSetChanged();
    }

    private String formatCurrency(long amount) {
        return String.format("%,d₫", amount);
    }
}