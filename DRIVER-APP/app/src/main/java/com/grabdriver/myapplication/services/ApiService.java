package com.grabdriver.myapplication.services;

import com.grabdriver.myapplication.models.AcceptOrderRequest;
import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.DriverOrderResponse;
import com.grabdriver.myapplication.models.EarningsResponse;
import com.grabdriver.myapplication.models.FeedbackRequest;
import com.grabdriver.myapplication.models.LocationUpdateRequest;
import com.grabdriver.myapplication.models.LoginRequest;
import com.grabdriver.myapplication.models.LoginResponse;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.models.ProfileStatistics;
import com.grabdriver.myapplication.models.RejectOrderRequest;
import com.grabdriver.myapplication.models.Reward;
import com.grabdriver.myapplication.models.RewardResponse;
import com.grabdriver.myapplication.models.Shipper;
import com.grabdriver.myapplication.models.ShipperProfileUpdate;
import com.grabdriver.myapplication.models.Transaction;
import com.grabdriver.myapplication.models.UpdateStatusRequest;
import com.grabdriver.myapplication.models.Wallet;
import com.grabdriver.myapplication.models.WithdrawRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    // Authentication APIs
    @POST("api/driver/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);
    
    @POST("api/driver/logout")
    Call<ApiResponse<Void>> logout();
    
    @GET("api/driver/verify-token")
    Call<ApiResponse<Boolean>> verifyToken();
    
    // Location & Tracking APIs
    @POST("api/driver/location/update")
    Call<ApiResponse<Void>> updateLocation(@Body LocationUpdateRequest locationRequest);
    
    @GET("api/driver/location/current")
    Call<ApiResponse<Map<String, Double>>> getCurrentLocation();
    
    // Order Management APIs
    @GET("api/driver/orders/available")
    Call<ApiResponse<List<DriverOrderResponse>>> getAvailableOrders(@Query("page") int page, @Query("size") int size);
    
    @GET("api/driver/orders/assigned")
    Call<ApiResponse<List<DriverOrderResponse>>> getAssignedOrders(@Query("page") int page, @Query("size") int size);
    
    @GET("api/driver/orders/history")
    Call<ApiResponse<List<DriverOrderResponse>>> getOrderHistory(@Query("page") int page, @Query("size") int size, @QueryMap Map<String, String> filters);
    
    @GET("api/driver/orders/{orderId}/details")
    Call<ApiResponse<DriverOrderResponse>> getOrderDetails(@Path("orderId") long orderId);
    
    @POST("api/driver/orders/{orderId}/accept")
    Call<ApiResponse<Order>> acceptOrder(@Path("orderId") long orderId, @Body AcceptOrderRequest request);
    
    @POST("api/driver/orders/{orderId}/reject")
    Call<ApiResponse<Void>> rejectOrder(@Path("orderId") long orderId, @Body RejectOrderRequest request);
    
    @PUT("api/driver/orders/{orderId}/status")
    Call<ApiResponse<Order>> updateOrderStatus(@Path("orderId") long orderId, @Query("status") String status, @Body UpdateStatusRequest request);
    
    @POST("api/driver/orders/{orderId}/pickup-confirm")
    Call<ApiResponse<Order>> confirmPickup(@Path("orderId") long orderId);
    
    @POST("api/driver/orders/{orderId}/delivery-confirm")
    Call<ApiResponse<Order>> confirmDelivery(@Path("orderId") long orderId);
    
    @GET("api/driver/orders/pending-count")
    Call<ApiResponse<Integer>> getPendingOrdersCount();
    
    // Profile Management APIs
    @GET("api/driver/profile")
    Call<ApiResponse<Shipper>> getProfile();
    
    @PUT("api/driver/profile")
    Call<ApiResponse<Shipper>> updateProfile(@Body ShipperProfileUpdate profileUpdate);
    
    @GET("api/driver/profile/stats")
    Call<ApiResponse<ProfileStatistics>> getProfileStats();
    
    @Multipart
    @POST("api/driver/profile/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part avatar);
    
    // Wallet & Financial APIs
    @GET("api/driver/wallet")
    Call<ApiResponse<Wallet>> getWalletInfo();
    
    @GET("api/driver/wallet/transactions")
    Call<ApiResponse<List<Transaction>>> getTransactions(@Query("page") int page, @Query("size") int size);
    
    @GET("api/driver/wallet/transactions/type")
    Call<ApiResponse<List<Transaction>>> getTransactionsByType(@Query("type") String type, @Query("page") int page, @Query("size") int size);
    
    @POST("api/driver/wallet/withdraw")
    Call<ApiResponse<Transaction>> withdrawMoney(@Body WithdrawRequest request);
    
    @GET("api/driver/wallet/earnings")
    Call<ApiResponse<EarningsResponse>> getEarnings(@Query("period") String period);
    
    @GET("api/driver/wallet/can-withdraw")
    Call<ApiResponse<Boolean>> canWithdraw(@Query("amount") double amount);
    
    // Rewards System APIs
    @GET("api/driver/rewards/available")
    Call<ApiResponse<List<RewardResponse>>> getAvailableRewards();
    
    @GET("api/driver/rewards/claimed")
    Call<ApiResponse<List<RewardResponse>>> getClaimedRewards();
    
    @POST("api/driver/rewards/{rewardId}/claim")
    Call<ApiResponse<Reward>> claimReward(@Path("rewardId") long rewardId);
    
    @GET("api/driver/rewards/progress")
    Call<ApiResponse<List<RewardResponse>>> getRewardProgress();
    
    // Analytics APIs
    @GET("api/driver/analytics/performance")
    Call<ApiResponse<Map<String, Object>>> getPerformanceAnalytics(@Query("period") String period);
    
    @GET("api/driver/analytics/earnings")
    Call<ApiResponse<Map<String, Object>>> getEarningsAnalytics(@Query("period") String period);
    
    @GET("api/driver/analytics/orders")
    Call<ApiResponse<Map<String, Object>>> getOrdersAnalytics(@Query("period") String period);
    
    // System Utilities APIs
    @GET("api/driver/system/version")
    Call<ApiResponse<Map<String, String>>> checkVersion(@Query("currentVersion") String currentVersion);
    
    @POST("api/driver/system/feedback")
    Call<ApiResponse<Void>> submitFeedback(@Body FeedbackRequest feedback);
    
    @GET("api/driver/system/support")
    Call<ApiResponse<Map<String, String>>> getSupportInfo();
} 