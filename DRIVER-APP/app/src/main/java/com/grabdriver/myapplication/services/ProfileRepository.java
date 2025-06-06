package com.grabdriver.myapplication.services;

import android.content.Context;
import android.net.Uri;

import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.ProfileStatistics;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.models.ShipperProfileUpdate;
import com.grabdriver.myapplication.utils.SessionManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ProfileRepository extends ApiRepository {
    private SessionManager sessionManager;

    public ProfileRepository(Context context) {
        super(context);
        this.sessionManager = new SessionManager(context);
    }

    // Lấy thông tin tài xế
    public void getProfile(NetworkCallback<Shipper> callback) {
        Call<ApiResponse<Shipper>> call = getApiService().getProfile();
        
        executeCall(call, new NetworkCallback<Shipper>() {
            @Override
            public void onSuccess(Shipper result) {
                // Cập nhật thông tin tài xế trong SessionManager
                if (result != null) {
                    sessionManager.updateShipperInfo(result);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Cập nhật thông tin tài xế
    public void updateProfile(String name, String email, String vehicleType, String licensePlate, NetworkCallback<Shipper> callback) {
        ShipperProfileUpdate profileUpdate = new ShipperProfileUpdate(name, email, vehicleType, licensePlate);
        Call<ApiResponse<Shipper>> call = getApiService().updateProfile(profileUpdate);
        
        executeCall(call, new NetworkCallback<Shipper>() {
            @Override
            public void onSuccess(Shipper result) {
                // Cập nhật thông tin tài xế trong SessionManager
                if (result != null) {
                    sessionManager.updateShipperInfo(result);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy thống kê tài khoản
    public void getProfileStats(NetworkCallback<ProfileStatistics> callback) {
        Call<ApiResponse<ProfileStatistics>> call = getApiService().getProfileStats();
        executeCall(call, callback);
    }
    
    // Upload ảnh đại diện
    public void uploadAvatar(Uri imageUri, Context context, NetworkCallback<String> callback) {
        try {
            // Chuyển đổi Uri thành File
            File file = new File(imageUri.getPath());
            
            // Tạo RequestBody từ file
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            
            // Tạo MultipartBody.Part từ RequestBody
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
            
            // Gọi API
            Call<ApiResponse<String>> call = getApiService().uploadAvatar(body);
            executeCall(call, callback);
        } catch (Exception e) {
            callback.onError("Lỗi khi xử lý file: " + e.getMessage());
        }
    }
} 