package com.grabdriver.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.services.LocationService;
import com.grabdriver.myapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationService.LocationUpdateListener {
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;

    // UI Components
    private TextView tvOrderInfo;
    private Button btnNavigation;
    private Button btnCallCustomer;
    private Button btnCompleteOrder;

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

        tvOrderInfo = findViewById(R.id.tv_order_info);
        btnNavigation = findViewById(R.id.btn_navigation);
        btnCallCustomer = findViewById(R.id.btn_call_customer);
        btnCompleteOrder = findViewById(R.id.btn_complete_order);
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
        // API call to load order details would be implemented here
        // For demo purposes, using mock data
        currentOrder = createDemoOrder(orderId);
        updateOrderInfo();
        setupMapLocations();
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
            String orderInfo = String.format(
                    "Đơn #%d\n👤 %s\n📍 %s\n📞 %s",
                    currentOrder.getId(),
                    currentOrder.getCustomerName(),
                    currentOrder.getAddress(),
                    currentOrder.getCustomerPhone());
            tvOrderInfo.setText(orderInfo);
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
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable location if permission granted
        enableLocationOnMap();

        // Add markers and route
        addMapMarkers();
        getCurrentLocationAndRoute();
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
        Toast.makeText(this, "Chức năng hoàn thành đơn hàng sẽ được cập nhật", Toast.LENGTH_SHORT).show();
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