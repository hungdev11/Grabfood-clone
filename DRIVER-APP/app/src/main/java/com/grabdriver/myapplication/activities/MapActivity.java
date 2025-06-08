package com.grabdriver.myapplication.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.repository.ApiManager;
import com.grabdriver.myapplication.repository.ApiRepository;
import com.grabdriver.myapplication.services.LocationService;
import com.grabdriver.myapplication.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationService.LocationUpdateListener {
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;
    private ApiManager apiManager;

    // UI Components
    private TextView tvOrderInfo;
    private Button btnNavigation;
    private Button btnCallCustomer;
    private Button btnCompleteOrder;
    private FloatingActionButton fabMyLocation;

    // Map markers
    private Marker currentLocationMarker;
    private Marker destinationMarker;
    private Marker restaurantMarker;
    private Polyline routePolyline;

    // Order data
    private Order currentOrder;
    private LatLng currentLocation;
    private LatLng restaurantLocation;
    private LatLng deliveryLocation;
    private boolean hasAutoLoadedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeComponents();
        setupMapFragment();
        setupClickListeners();
        loadOrderData();

        // Set location update listener
        LocationService.setLocationUpdateListener(this);
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiManager = ApiManager.getInstance(this);

        tvOrderInfo = findViewById(R.id.tv_order_info);
        btnNavigation = findViewById(R.id.btn_navigation);
        btnCallCustomer = findViewById(R.id.btn_call_customer);
        btnCompleteOrder = findViewById(R.id.btn_complete_order);
        fabMyLocation = findViewById(R.id.fab_my_location);
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        btnNavigation.setOnClickListener(v -> openGoogleMapsNavigation());
        btnCallCustomer.setOnClickListener(v -> callCustomer());
        btnCompleteOrder.setOnClickListener(v -> completeOrder());
        fabMyLocation.setOnClickListener(v -> centerOnMyLocation());
        
        // Update button states based on order status
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        if (currentOrder != null) {
            String status = currentOrder.getStatus();
            boolean canComplete = "PROCESSING".equals(status) || "READY_FOR_PICKUP".equals(status) || "SHIPPING".equals(status);
            boolean canCall = canComplete;
            
            btnCompleteOrder.setEnabled(canComplete);
            btnCallCustomer.setEnabled(canCall);
            
            // Update button text based on status
            if ("PROCESSING".equals(status) || "READY_FOR_PICKUP".equals(status)) {
                btnCompleteOrder.setText("Hoàn thành đơn hàng");
            } else if ("SHIPPING".equals(status)) {
                btnCompleteOrder.setText("Xác nhận giao hàng");
            } else {
                btnCompleteOrder.setText("Đã hoàn thành");
            }
        }
    }

    private void loadOrderData() {
        // Get order data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("order_id")) {
            long orderId = intent.getLongExtra("order_id", 0);
            loadOrderById(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrderById(long orderId) {
        if (apiManager != null) {
            apiManager.getOrderRepository().getOrderDetails(orderId, new ApiRepository.NetworkCallback<Order>() {
                @Override
                public void onSuccess(Order order) {
                    runOnUiThread(() -> {
                        currentOrder = order;
                        updateOrderInfo();
                        setupMapLocations();
                        
                        // Refresh map if ready
                        if (googleMap != null) {
                            addMapMarkers();
                            autoLoadCurrentLocationAndRoute();
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(MapActivity.this, "Lỗi tải thông tin đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                        // Fallback to demo data
                        currentOrder = createDemoOrder(orderId);
                        updateOrderInfo();
                        setupMapLocations();
                    });
                }
            });
        } else {
            // Fallback to demo data
            currentOrder = createDemoOrder(orderId);
            updateOrderInfo();
            setupMapLocations();
        }
    }

    private Order createDemoOrder(long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setAddress("123 Nguyễn Văn Linh, Q.7, TP.HCM");
        order.setCustomerName("Nguyễn Văn A");
        order.setCustomerPhone("0123456789");
        order.setStatus("SHIPPING");
        order.setDeliveryLatitude(10.7769);
        order.setDeliveryLongitude(106.7009);
        order.setRestaurantLatitude(10.7829);
        order.setRestaurantLongitude(106.6959);
        return order;
    }

    private void updateOrderInfo() {
        if (currentOrder != null) {
            String statusText = getStatusText(currentOrder.getStatus());
            String priceText = currentOrder.getTotalPrice() != null && currentOrder.getTotalPrice().compareTo(BigDecimal.ZERO) > 0 ? 
                String.format("💰 %,d VNĐ", currentOrder.getTotalPrice().longValue()) : "";
            
            String orderInfo = String.format(
                    "Đơn #%d - %s\n👤 %s\n📍 %s\n📞 %s\n%s",
                    currentOrder.getId(),
                    statusText,
                    currentOrder.getCustomerName(),
                    currentOrder.getAddress(),
                    currentOrder.getCustomerPhone(),
                    priceText);
            tvOrderInfo.setText(orderInfo);
            
            // Update button states
            updateButtonStates();
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "PROCESSING":
                return "Đang xử lý";
            case "READY_FOR_PICKUP":
                return "Sẵn sàng lấy hàng";
            case "SHIPPING":
                return "Đang giao hàng";
            case "COMPLETED":
                return "Đã hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            case "REJECTED":
                return "Đã từ chối";
            default:
                return status;
        }
    }

    private void setupMapLocations() {
        if (currentOrder != null) {
            restaurantLocation = new LatLng(
                    currentOrder.getRestaurantLatitude(),
                    currentOrder.getRestaurantLongitude());
            deliveryLocation = new LatLng(
                    currentOrder.getDeliveryLatitude(),
                    currentOrder.getDeliveryLongitude());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Configure map
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Use custom button instead

        // Enable location if permission granted
        enableLocationOnMap();

        // Add markers and route
        addMapMarkers();
        
        // Auto-center on current location when map loads
        autoLoadCurrentLocationAndRoute();
    }

    private void enableLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void addMapMarkers() {
        if (googleMap == null)
            return;

        // Add restaurant marker
        if (restaurantLocation != null) {
            restaurantMarker = googleMap.addMarker(new MarkerOptions()
                    .position(restaurantLocation)
                    .title("Nhà hàng")
                    .snippet("Điểm lấy hàng")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        // Add delivery location marker
        if (deliveryLocation != null) {
            destinationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(deliveryLocation)
                    .title("Điểm giao hàng")
                    .snippet(currentOrder != null ? currentOrder.getAddress() : "")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private void getCurrentLocationAndRoute() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Add current location marker
                updateCurrentLocationMarker();

                // Draw route
                drawRouteToDestination();

                // Fit all markers in view
                fitMarkersInView();
            } else {
                Log.w(TAG, "Failed to get location.");
            }
        });
    }

    // private void autoLoadCurrentLocationAndRoute() {
    //     if (ActivityCompat.checkSelfPermission(this,
    //             Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    //         requestLocationPermissions();
    //         return;
    //     }

    //     Task<Location> locationResult = fusedLocationClient.getLastLocation();
    //     locationResult.addOnCompleteListener(this, task -> {
    //         if (task.isSuccessful() && task.getResult() != null) {
    //             Location location = task.getResult();
    //             currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

    //             // Add current location marker
    //             updateCurrentLocationMarker();

    //             // Draw route
    //             drawRouteToDestination();

    //             // Only auto-center on first load
    //             if (!hasAutoLoadedLocation) {
    //                 hasAutoLoadedLocation = true;
                    
    //                 // Auto-center on current location first, then show all markers
    //                 googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f), 1000, new GoogleMap.CancelableCallback() {
    //                     @Override
    //                     public void onFinish() {
    //                         // After centering on current location, fit all markers in view with animation
    //                         googleMap.postDelayed(() -> fitMarkersInView(), 1500);
    //                     }

    //                     @Override
    //                     public void onCancel() {
    //                         // If animation is cancelled, still fit markers
    //                         fitMarkersInView();
    //                     }
    //                 });

    //                 Toast.makeText(this, "Đã tải vị trí hiện tại", Toast.LENGTH_SHORT).show();
    //             } else {
    //                 // Just update route without changing camera position
    //                 fitMarkersInView();
    //             }
    //         } else {
    //             Log.w(TAG, "Failed to get current location on auto-load.");
    //             // Fallback to fit all markers
    //             fitMarkersInView();
    //         }
    //     });
    // }

    private void updateCurrentLocationMarker() {
        if (googleMap != null && currentLocation != null) {
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }

            currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title("Vị trí hiện tại")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }

    private void drawRouteToDestination() {
        if (currentLocation != null && deliveryLocation != null) {
            List<LatLng> routePoints = new ArrayList<>();
            routePoints.add(currentLocation);

            // Add restaurant location if order is not picked up yet
            if (currentOrder != null && "ASSIGNED".equals(currentOrder.getStatus())) {
                routePoints.add(restaurantLocation);
            }

            routePoints.add(deliveryLocation);

            // Remove existing route
            if (routePolyline != null) {
                routePolyline.remove();
            }

            // Draw new route
            routePolyline = googleMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .width(8f)
                    .color(Color.BLUE)
                    .geodesic(true));
        }
    }

    private void fitMarkersInView() {
        if (googleMap == null)
            return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (currentLocation != null)
            builder.include(currentLocation);
        if (restaurantLocation != null)
            builder.include(restaurantLocation);
        if (deliveryLocation != null)
            builder.include(deliveryLocation);

        try {
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error fitting markers in view", e);
        }
    }

    private void centerOnMyLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }

        // Get current location
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                
                // Update current location
                currentLocation = latLng;
                updateCurrentLocationMarker();
                
                // Animate camera to current location with appropriate zoom
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                
                Toast.makeText(this, "Đã quay lại vị trí hiện tại", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Failed to get current location.");
                Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGoogleMapsNavigation() {
        if (deliveryLocation != null) {
            String uri = String.format(
                    "google.navigation:q=%f,%f&mode=d",
                    deliveryLocation.latitude,
                    deliveryLocation.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Google Maps không được cài đặt", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callCustomer() {
        if (currentOrder != null && currentOrder.getCustomerPhone() != null) {
            // Check if order can be handled
            String status = currentOrder.getStatus();
            if (!"PROCESSING".equals(status) && !"READY_FOR_PICKUP".equals(status) && !"SHIPPING".equals(status)) {
                Toast.makeText(this, "Không thể gọi khách hàng cho đơn hàng này", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + currentOrder.getCustomerPhone()));

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cần quyền gọi điện", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void completeOrder() {
        if (currentOrder == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String status = currentOrder.getStatus();
        
        // Check if order can be completed
        if (!"PROCESSING".equals(status) && !"READY_FOR_PICKUP".equals(status) && !"SHIPPING".equals(status)) {
            String statusMessage = getStatusMessage(status);
            Toast.makeText(this, "Đơn hàng này " + statusMessage + ". Không thể hoàn thành.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận hoàn thành đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hoàn thành đơn hàng #" + currentOrder.getId() + "?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    performCompleteOrder();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void performCompleteOrder() {
        // Show loading
        btnCompleteOrder.setEnabled(false);
        btnCompleteOrder.setText("Đang xử lý...");
        
        if (apiManager != null) {
            apiManager.getOrderRepository().confirmDelivery(currentOrder.getId(), 
                new ApiRepository.NetworkCallback<Order>() {
                    @Override
                    public void onSuccess(Order updatedOrder) {
                        runOnUiThread(() -> {
                            // Update current order
                            currentOrder = updatedOrder;
                            updateOrderInfo();
                            
                            // Show success message
                            Toast.makeText(MapActivity.this, "Đã giao hàng thành công!", Toast.LENGTH_LONG).show();
                            
                            // Return to previous screen after delay
                            new android.os.Handler().postDelayed(() -> {
                                finish();
                            }, 2000);
                        });
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            // Reset button state
                            btnCompleteOrder.setEnabled(true);
                            updateButtonStates();
                            
                            Toast.makeText(MapActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
        } else {
            // Reset button state
            btnCompleteOrder.setEnabled(true);
            updateButtonStates();
            Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getStatusMessage(String status) {
        switch (status) {
            case "CANCELLED":
                return "đã bị hủy";
            case "REJECTED":
                return "đã bị từ chối";
            case "COMPLETED":
                return "đã hoàn thành";
            default:
                return "không khả dụng";
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        runOnUiThread(() -> {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            updateCurrentLocationMarker();
            drawRouteToDestination();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationOnMap();
                getCurrentLocationAndRoute();
            } else {
                Toast.makeText(this, "Cần quyền vị trí để sử dụng bản đồ", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationService.setLocationUpdateListener(null);
    }
}