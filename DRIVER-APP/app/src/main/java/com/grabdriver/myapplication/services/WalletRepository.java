package com.grabdriver.myapplication.services;

import android.content.Context;

import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.EarningsResponse;
import com.grabdriver.myapplication.models.Transaction;
import com.grabdriver.myapplication.models.Wallet;
import com.grabdriver.myapplication.models.WithdrawRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class WalletRepository extends ApiRepository {

    public WalletRepository(Context context) {
        super(context);
    }

    // Lấy thông tin ví
    public void getWalletInfo(NetworkCallback<Wallet> callback) {
        Call<ApiResponse<Wallet>> call = getApiService().getWalletInfo();
        executeCall(call, callback);
    }
    
    // Lấy lịch sử giao dịch
    public void getTransactions(int page, int size, NetworkCallback<List<Transaction>> callback) {
        Call<ApiResponse<List<Transaction>>> call = getApiService().getTransactions(page, size);
        executeCall(call, callback);
    }
    
    // Lấy lịch sử giao dịch theo loại
    public void getTransactionsByType(String type, int page, int size, NetworkCallback<List<Transaction>> callback) {
        Call<ApiResponse<List<Transaction>>> call = getApiService().getTransactionsByType(type, page, size);
        executeCall(call, callback);
    }
    
    // Rút tiền
    public void withdrawMoney(double amount, String bankAccount, String bankName, String note, NetworkCallback<Transaction> callback) {
        WithdrawRequest request = new WithdrawRequest(amount, bankAccount, bankName, note);
        Call<ApiResponse<Transaction>> call = getApiService().withdrawMoney(request);
        executeCall(call, callback);
    }
    
    // Lấy thống kê thu nhập
    public void getEarnings(String period, NetworkCallback<EarningsResponse> callback) {
        Call<ApiResponse<EarningsResponse>> call = getApiService().getEarnings(period);
        executeCall(call, callback);
    }
    
    // Kiểm tra có thể rút tiền
    public void canWithdraw(double amount, NetworkCallback<Boolean> callback) {
        Call<ApiResponse<Boolean>> call = getApiService().canWithdraw(amount);
        executeCall(call, callback);
    }
} 