package com.grabdriver.myapplication.services;

import android.content.Context;

import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.LoginRequest;
import com.grabdriver.myapplication.models.LoginResponse;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.utils.SessionManager;

import retrofit2.Call;

public class AuthRepository extends ApiRepository {
    private SessionManager sessionManager;

    public AuthRepository(Context context) {
        super(context);
        this.sessionManager = new SessionManager(context);
    }

    public void login(String phone, String password, boolean rememberMe, NetworkCallback<LoginResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(phone, password);
        Call<ApiResponse<LoginResponse>> call = getApiService().login(loginRequest);
        
        executeCall(call, new NetworkCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                // Lưu thông tin đăng nhập vào SessionManager
                if (result != null && result.getShipperInfo() != null) {
                    sessionManager.createLoginSession(
                            result.getToken(),
                            result.getShipperInfo(),
                            rememberMe
                    );
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    public void logout(NetworkCallback<Void> callback) {
        Call<ApiResponse<Void>> call = getApiService().logout();
        executeCall(call, new NetworkCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Xóa thông tin đăng nhập khỏi SessionManager
                sessionManager.logout();
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    public void verifyToken(NetworkCallback<Boolean> callback) {
        Call<ApiResponse<Boolean>> call = getApiService().verifyToken();
        executeCall(call, callback);
    }
} 