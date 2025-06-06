package com.grabdriver.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
// Temporarily comment Firebase
// import com.google.firebase.messaging.FirebaseMessaging;
import com.grabdriver.myapplication.fragments.HomeFragment;
import com.grabdriver.myapplication.fragments.OrdersFragment;
import com.grabdriver.myapplication.fragments.WalletFragment;
import com.grabdriver.myapplication.fragments.RewardsFragment;
import com.grabdriver.myapplication.fragments.ProfileFragment;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.services.ApiManager;
import com.grabdriver.myapplication.services.ApiRepository;
import com.grabdriver.myapplication.services.LocationService;
import com.grabdriver.myapplication.services.OrderUpdateService;
import com.grabdriver.myapplication.utils.SessionManager;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements OrderUpdateService.OrderUpdateListener {
    private static final int RC_LOCATION_PERMISSION = 123;
    private static final int RC_NOTIFICATION_PERMISSION = 124;

    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private ApiManager apiManager;
    private boolean servicesStarted = false;

    // Permission launcher for Android 13+ notification permission
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    // Broadcast receiver for order updates
    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (OrderUpdateService.ACTION_NEW_ORDER.equals(action)) {
                Order order = intent.getParcelableExtra(OrderUpdateService.EXTRA_ORDER);
                if (order != null) {
                    handleNewOrder(order);
                }
            } else if (OrderUpdateService.ACTION_ORDER_UPDATE.equals(action)) {
                Order order = intent.getParcelableExtra(OrderUpdateService.EXTRA_ORDER);
                if (order != null) {
                    handleOrderUpdate(order);
                }
            } else if (OrderUpdateService.ACTION_ORDER_CANCELLED.equals(action)) {
                long orderId = intent.getLongExtra(OrderUpdateService.EXTRA_ORDER_ID, 0);
                handleOrderCancelled(orderId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn() || !sessionManager.isSessionValid()) {
            navigateToLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializePermissionLaunchers();
        setupBottomNavigation();
        requestPermissions();
        // initializeFirebaseMessaging();

        // Handle notification intent
        handleNotificationIntent(getIntent());

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void initializePermissionLaunchers() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    // Permission result handled automatically
                });
    }

    private void setupBottomNavigation() {
        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_orders) {
                selectedFragment = new OrdersFragment();
            } else if (item.getItemId() == R.id.nav_wallet) {
                selectedFragment = new WalletFragment();
            } else if (item.getItemId() == R.id.nav_rewards) {
                selectedFragment = new RewardsFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (EasyPermissions.hasPermissions(this, perms)) {
            onLocationPermissionGranted();
        } else {
            EasyPermissions.requestPermissions(this,
                    "Ứng dụng cần quyền vị trí để theo dõi và giao hàng",
                    RC_LOCATION_PERMISSION, perms);
        }

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @AfterPermissionGranted(RC_LOCATION_PERMISSION)
    private void onLocationPermissionGranted() {
        startLocationAndUpdateServices();
    }

    private void startLocationAndUpdateServices() {
        if (!servicesStarted && sessionManager.isLoggedIn()) {
            // Start location service
            Intent locationIntent = new Intent(this, LocationService.class);
            locationIntent.setAction("START_LOCATION_UPDATES");
            startService(locationIntent);

            // Start order update service
            Intent orderUpdateIntent = new Intent(this, OrderUpdateService.class);
            orderUpdateIntent.setAction("START_ORDER_UPDATES");
            startService(orderUpdateIntent);

            // Add order update listener
            OrderUpdateService.addOrderUpdateListener(this);

            servicesStarted = true;
        }
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            // Check if the app was opened from a notification
            if (intent.hasExtra("order_id")) {
                long orderId = intent.getLongExtra("order_id", 0);
                if (orderId > 0) {
                    // Navigate to order details or map
                    openOrderMap(orderId);
                }
            }
        }
    }

    private void openOrderMap(long orderId) {
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra("order_id", orderId);
        startActivity(mapIntent);
    }

    private void handleNewOrder(Order order) {
        // Update UI to show new order notification
        runOnUiThread(() -> {
            Toast.makeText(this, "Đơn hàng mới: #" + order.getId(), Toast.LENGTH_LONG).show();

            // Refresh current fragment if it's HomeFragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).refreshOrders();
            }
        });
    }

    private void handleOrderUpdate(Order order) {
        runOnUiThread(() -> {
            // Refresh fragments that might display order information
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).refreshOrders();
            } else if (currentFragment instanceof OrdersFragment) {
                ((OrdersFragment) currentFragment).refreshOrders();
            }
        });
    }

    private void handleOrderCancelled(long orderId) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Đơn hàng #" + orderId + " đã bị hủy", Toast.LENGTH_LONG).show();

            // Refresh current fragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).refreshOrders();
            } else if (currentFragment instanceof OrdersFragment) {
                ((OrdersFragment) currentFragment).refreshOrders();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check session validity when app resumes
        if (!sessionManager.isLoggedIn() || !sessionManager.isSessionValid()) {
            navigateToLogin();
            return;
        }

        // Register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(OrderUpdateService.ACTION_NEW_ORDER);
        filter.addAction(OrderUpdateService.ACTION_ORDER_UPDATE);
        filter.addAction(OrderUpdateService.ACTION_ORDER_CANCELLED);
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUpdateReceiver, filter);

        // Start services if not already started
        if (sessionManager.isLocationTrackingEnabled() &&
                EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocationAndUpdateServices();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove order update listener
        OrderUpdateService.removeOrderUpdateListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    // OrderUpdateService.OrderUpdateListener implementation
    @Override
    public void onNewOrder(Order order) {
        handleNewOrder(order);
    }

    @Override
    public void onOrderUpdate(Order order) {
        handleOrderUpdate(order);
    }

    @Override
    public void onOrderCancelled(long orderId) {
        handleOrderCancelled(orderId);
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        runOnUiThread(() -> {
            // Connection status changed
        });
    }

    // EasyPermissions callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Public method to get SessionManager instance
     * This can be called from fragments to access session data
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Public method to handle logout
     * This can be called from ProfileFragment or other fragments
     */
    public void logout() {
        // Hiển thị dialog loading
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_loading);
        builder.setCancelable(false);
        AlertDialog loadingDialog = builder.create();
        loadingDialog.show();

        // Dừng các dịch vụ
        if (servicesStarted) {
            // Dừng dịch vụ cập nhật vị trí
            Intent locationIntent = new Intent(this, LocationService.class);
            locationIntent.setAction("STOP_LOCATION_UPDATES");
            startService(locationIntent);

            // Dừng dịch vụ cập nhật đơn hàng
            Intent orderUpdateIntent = new Intent(this, OrderUpdateService.class);
            orderUpdateIntent.setAction("STOP_ORDER_UPDATES");
            startService(orderUpdateIntent);

            // Xóa order update listener
            OrderUpdateService.removeOrderUpdateListener(this);
            servicesStarted = false;
        }

        // Gọi API đăng xuất
        apiManager.getAuthRepository().logout(new ApiRepository.NetworkCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    // Đóng dialog loading
                    loadingDialog.dismiss();
                    
                    // Xóa thông tin đăng nhập và chuyển đến màn hình đăng nhập
                    navigateToLogin();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    // Đóng dialog loading
                    loadingDialog.dismiss();
                    
                    // Vẫn xóa thông tin đăng nhập và chuyển đến màn hình đăng nhập kể cả khi API lỗi
                    // Hiển thị thông báo lỗi
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                });
            }
        });
    }

    /**
     * Public method to handle going online/offline
     */
    public void setOnlineStatus(boolean online) {
        if (sessionManager.isOnline() != online) {
            // Cập nhật trạng thái trong SessionManager
            sessionManager.setOnlineStatus(online);
            
            // Gọi API cập nhật vị trí và trạng thái online
            if (sessionManager.getCurrentLocation() != null) {
                double latitude = sessionManager.getCurrentLocation().getLatitude();
                double longitude = sessionManager.getCurrentLocation().getLongitude();
                
                apiManager.getLocationRepository().updateLocation(
                    latitude, 
                    longitude, 
                    online, 
                    new ApiRepository.NetworkCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            // Không cần làm gì vì đã cập nhật SessionManager ở trên
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, 
                                    "Lỗi khi cập nhật trạng thái: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
            }
        }
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public void navigateToOrdersTab() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        }
    }
}