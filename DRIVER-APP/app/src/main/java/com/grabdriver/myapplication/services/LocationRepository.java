package com.grabdriver.myapplication.services;

import android.content.Context;

import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.LocationUpdateRequest;
import com.grabdriver.myapplication.utils.SessionManager;

import java.util.Map;

import retrofit2.Call;

public class LocationRepository extends ApiRepository {
    private SessionManager sessionManager;

    public LocationRepository(Context context) {
        super(context);
        this.sessionManager = new SessionManager(context);
    }

    public void updateLocation(double latitude, double longitude, boolean isOnline, NetworkCallback<Void> callback) {
        LocationUpdateRequest request = new LocationUpdateRequest(latitude, longitude, isOnline);
        Call<ApiResponse<Void>> call = getApiService().updateLocation(request);
        
        executeCall(call, new NetworkCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Cập nhật vị trí trong SessionManager
                sessionManager.updateCurrentLocation(latitude, longitude);
                sessionManager.setOnlineStatus(isOnline);
                
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void getCurrentLocation(NetworkCallback<Map<String, Double>> callback) {
        Call<ApiResponse<Map<String, Double>>> call = getApiService().getCurrentLocation();
        
        executeCall(call, new NetworkCallback<Map<String, Double>>() {
            @Override
            public void onSuccess(Map<String, Double> result) {
                if (result != null && result.containsKey("latitude") && result.containsKey("longitude")) {
                    // Cập nhật vị trí trong SessionManager
                    sessionManager.updateCurrentLocation(result.get("latitude"), result.get("longitude"));
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
} 