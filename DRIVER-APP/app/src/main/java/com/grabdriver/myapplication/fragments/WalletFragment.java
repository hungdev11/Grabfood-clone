package com.grabdriver.myapplication.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.grabdriver.myapplication.adapters.TransactionAdapter;
import com.grabdriver.myapplication.models.EarningsResponse;
import com.grabdriver.myapplication.models.Transaction;
import com.grabdriver.myapplication.models.Wallet;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WalletFragment extends Fragment {
    private TextView currentBalanceText;
    private TextView todayEarningsText;
    private TextView weekEarningsText;
    private TextView monthEarningsText;
    private TextView codHoldingText;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private Button withdrawButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressLoading;
    private TextView emptyTransactionsText;

    private ApiManager apiManager;
    private int currentPage = 0; // Spring pagination starts from 0
    private final int PAGE_SIZE = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        if (getActivity() instanceof MainActivity) {
            apiManager = ((MainActivity) getActivity()).getApiManager();
        }

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupWithdrawButton();
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
        withdrawButton = view.findViewById(R.id.btn_withdraw);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressLoading = view.findViewById(R.id.progress_loading);
        emptyTransactionsText = view.findViewById(R.id.text_empty_transactions);
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark);
    }

    private void setupWithdrawButton() {
        withdrawButton.setOnClickListener(v -> showWithdrawDialog());
    }

    private void loadWalletData() {
        showLoading(true);

        if (apiManager != null) {
            // Lấy thông tin ví
            apiManager.getWalletRepository().getWalletInfo(new ApiRepository.NetworkCallback<Wallet>() {
                @Override
                public void onSuccess(Wallet result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (result != null) {
                                currentBalanceText.setText(formatCurrency(result.getBalance()));
                                codHoldingText.setText(formatCurrency(result.getCodHolding()));
                                withdrawButton.setEnabled(result.getBalance() > 0);
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Error handled silently
                }
            });

            // Lấy thông tin thu nhập hôm nay
            apiManager.getWalletRepository().getEarnings(Constants.Period.TODAY,
                    new ApiRepository.NetworkCallback<EarningsResponse>() {
                        @Override
                        public void onSuccess(EarningsResponse result) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (result != null) {
                                        todayEarningsText.setText(formatCurrency(result.getTodayEarnings()));
                                    } else {
                                        todayEarningsText.setText(formatCurrency(0));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Error handled silently
                        }
                    });

            // Lấy thông tin thu nhập tuần này
            apiManager.getWalletRepository().getEarnings(Constants.Period.WEEK,
                    new ApiRepository.NetworkCallback<EarningsResponse>() {
                        @Override
                        public void onSuccess(EarningsResponse result) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (result != null) {
                                        weekEarningsText.setText(formatCurrency(result.getWeekEarnings()));
                                    } else {
                                        weekEarningsText.setText(formatCurrency(0));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Error handled silently
                        }
                    });

            // Lấy thông tin thu nhập tháng này
            apiManager.getWalletRepository().getEarnings(Constants.Period.MONTH,
                    new ApiRepository.NetworkCallback<EarningsResponse>() {
                        @Override
                        public void onSuccess(EarningsResponse result) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (result != null) {
                                        monthEarningsText.setText(formatCurrency(result.getMonthEarnings()));
                                    } else {
                                        monthEarningsText.setText(formatCurrency(0));
                                    }

                                    showLoading(false);
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    showLoading(false);
                                });
                            }
                        }
                    });
        } else {
            showLoading(false);
        }
    }

    private void loadTransactions() {
        showLoading(true);

        if (apiManager != null) {
            apiManager.getWalletRepository().getTransactions(currentPage, PAGE_SIZE,
                    new ApiRepository.NetworkCallback<List<Transaction>>() {
                        @Override
                        public void onSuccess(List<Transaction> result) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    showLoading(false);

                                    if (result != null && !result.isEmpty()) {
                                        transactionList.clear();
                                        transactionList.addAll(result);
                                        transactionAdapter.notifyDataSetChanged();
                                        showEmptyTransactions(false);
                                    } else {
                                        transactionList.clear();
                                        transactionAdapter.notifyDataSetChanged();
                                        showEmptyTransactions(true);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    showLoading(false);
                                    showEmptyTransactions(true);
                                    Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    });
        } else {
            showLoading(false);
            showEmptyTransactions(true);
        }
    }

    private void showWithdrawDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_withdraw, null);
        EditText amountInput = dialogView.findViewById(R.id.input_amount);
        EditText bankAccountInput = dialogView.findViewById(R.id.input_bank_account);
        EditText bankNameInput = dialogView.findViewById(R.id.input_bank_name);
        EditText noteInput = dialogView.findViewById(R.id.input_note);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Rút tiền")
                .setView(dialogView)
                .setPositiveButton("Rút tiền", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String amountStr = amountInput.getText().toString().trim();
                String bankAccount = bankAccountInput.getText().toString().trim();
                String bankName = bankNameInput.getText().toString().trim();
                String note = noteInput.getText().toString().trim();

                if (amountStr.isEmpty() || bankAccount.isEmpty() || bankName.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        Toast.makeText(getContext(), "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Gọi API kiểm tra có thể rút tiền không
                    withdrawMoney(amount, bankAccount, bankName, note);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void withdrawMoney(double amount, String bankAccount, String bankName, String note) {
        if (apiManager != null) {
            showLoading(true);

            // Kiểm tra có thể rút tiền không
            apiManager.getWalletRepository().canWithdraw(amount, new ApiRepository.NetworkCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (getActivity() != null) {
                        if (result != null && result) {
                            // Nếu có thể rút tiền, thực hiện rút tiền
                            apiManager.getWalletRepository().withdrawMoney(
                                    amount, bankAccount, bankName, note,
                                    new ApiRepository.NetworkCallback<Transaction>() {
                                        @Override
                                        public void onSuccess(Transaction result) {
                                            getActivity().runOnUiThread(() -> {
                                                showLoading(false);
                                                Toast.makeText(getContext(), "Đã gửi yêu cầu rút tiền thành công",
                                                        Toast.LENGTH_SHORT).show();

                                                // Cập nhật lại dữ liệu
                                                refreshData();
                                            });
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            getActivity().runOnUiThread(() -> {
                                                showLoading(false);
                                                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT)
                                                        .show();
                                            });
                                        }
                                    });
                        } else {
                            getActivity().runOnUiThread(() -> {
                                showLoading(false);
                                Toast.makeText(getContext(), "Không thể rút tiền với số tiền này", Toast.LENGTH_SHORT)
                                        .show();
                            });
                        }
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

    private void refreshData() {
        currentPage = 0; // Reset to first page (0-based)
        loadWalletData();
        loadTransactions();
    }

    private void showLoading(boolean isLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
        if (progressLoading != null) {
            progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyTransactions(boolean isEmpty) {
        if (emptyTransactionsText != null) {
            emptyTransactionsText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (transactionsRecyclerView != null) {
            transactionsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private String formatCurrency(long amount) {
        return String.format("%,d₫", amount);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
}