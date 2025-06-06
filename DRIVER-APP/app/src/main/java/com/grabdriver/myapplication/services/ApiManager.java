package com.grabdriver.myapplication.services;

import android.content.Context;

/**
 * Lớp quản lý trung tâm cho tất cả các API repositories
 */
public class ApiManager {
    private static ApiManager instance;
    private final Context context;
    
    // Các repositories
    private AuthRepository authRepository;
    private LocationRepository locationRepository;
    private OrderRepository orderRepository;
    private ProfileRepository profileRepository;
    private WalletRepository walletRepository;
    private RewardRepository rewardRepository;
    
    private ApiManager(Context context) {
        this.context = context.getApplicationContext();
        initRepositories();
    }
    
    /**
     * Khởi tạo singleton instance của ApiManager
     */
    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }
    
    /**
     * Khởi tạo tất cả repositories
     */
    private void initRepositories() {
        authRepository = new AuthRepository(context);
        locationRepository = new LocationRepository(context);
        orderRepository = new OrderRepository(context);
        profileRepository = new ProfileRepository(context);
        walletRepository = new WalletRepository(context);
        rewardRepository = new RewardRepository(context);
    }
    
    /**
     * Lấy AuthRepository để quản lý API xác thực
     */
    public AuthRepository getAuthRepository() {
        return authRepository;
    }
    
    /**
     * Lấy LocationRepository để quản lý API vị trí
     */
    public LocationRepository getLocationRepository() {
        return locationRepository;
    }
    
    /**
     * Lấy OrderRepository để quản lý API đơn hàng
     */
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
    
    /**
     * Lấy ProfileRepository để quản lý API thông tin tài xế
     */
    public ProfileRepository getProfileRepository() {
        return profileRepository;
    }
    
    /**
     * Lấy WalletRepository để quản lý API ví
     */
    public WalletRepository getWalletRepository() {
        return walletRepository;
    }
    
    public RewardRepository getRewardRepository() {
        return rewardRepository;
    }
} 