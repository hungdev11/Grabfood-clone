package com.grabdriver.fe.fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.grabdriver.fe.R;
import com.grabdriver.fe.data.MockDataManager;
import com.grabdriver.fe.models.Order;
import com.grabdriver.fe.models.Wallet;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private TextView tvShipperName, tvGems, tvTodayOrders, tvTodayEarnings;
    private TextView tvRating, tvLocationStatus;
    private TextView tvWalletBalance, tvAccountStatus;
    private Switch switchOnlineStatus;
    private MaterialButton btnRefreshLocation;
    private SharedPreferences sharedPreferences;
    
    // Google Maps and Location
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        sharedPreferences = requireActivity().getSharedPreferences("GrabDriverPrefs", MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        initViews(view);
        loadDashboardData();
        setupOnlineStatusToggle();
        setupLocationRequest();
        setupMap();
        setupRefreshButton();
        
        return view;
    }

    private void initViews(View view) {
        tvShipperName = view.findViewById(R.id.tv_shipper_name);
        tvGems = view.findViewById(R.id.tv_total_gems);
        tvTodayOrders = view.findViewById(R.id.tv_today_orders);
        tvTodayEarnings = view.findViewById(R.id.tv_today_earnings);
        tvRating = view.findViewById(R.id.tv_rating);
        tvLocationStatus = view.findViewById(R.id.tv_location_status);
        tvWalletBalance = view.findViewById(R.id.tv_wallet_balance);
        tvAccountStatus = view.findViewById(R.id.tv_account_status);
        switchOnlineStatus = view.findViewById(R.id.switch_online_status);
        btnRefreshLocation = view.findViewById(R.id.btn_refresh_location);
    }

    private void setupLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationOnMap(location);
                    break; // Only use the first location
                }
            }
        };
    }

    private void setupRefreshButton() {
        btnRefreshLocation.setOnClickListener(v -> {
            tvLocationStatus.setText("Đang tải...");
            btnRefreshLocation.setEnabled(false);
            requestCurrentLocation();
        });
    }

    private void loadDashboardData() {
        // Load shipper data from SharedPreferences
        String shipperName = sharedPreferences.getString("shipperName", "Tài xế");
        int gems = sharedPreferences.getInt("gems", 0);
        float rating = sharedPreferences.getFloat("rating", 0.0f);
        boolean isOnline = sharedPreferences.getBoolean("isOnline", false);

        // Set basic info
        tvShipperName.setText("Xin chào, " + shipperName + "!");
        tvGems.setText(String.valueOf(gems));
        tvRating.setText(String.format(Locale.getDefault(), "%.1f ⭐", rating));
        switchOnlineStatus.setChecked(isOnline);

        // Load wallet information
        String shipperId = sharedPreferences.getString("shipperId", "SH001");
        Wallet wallet = MockDataManager.createMockWallet(shipperId);
        
        tvWalletBalance.setText(wallet.getFormattedBalance());
        tvAccountStatus.setText(wallet.getAccountStatus());
        
        // Set account status color
        int statusColor;
        if (wallet.isEligibleForCOD()) {
            statusColor = getResources().getColor(R.color.success_color, null);
        } else {
            statusColor = getResources().getColor(R.color.warning_color, null);
        }
        tvAccountStatus.setTextColor(statusColor);

        // Calculate today's stats from mock orders
        calculateTodayStats();
    }

    private void calculateTodayStats() {
        List<Order> orders = MockDataManager.createMockOrders();
        int todayOrders = 0;
        long todayEarnings = 0;

        // Count completed orders for today (mock calculation)
        for (Order order : orders) {
            if ("completed".equals(order.getStatus())) {
                todayOrders++;
                todayEarnings += order.getDeliveryFee();
            }
        }

        // Add some mock data for demonstration
        todayOrders += 15; // Additional completed orders
        todayEarnings += 380000; // Additional earnings

        tvTodayOrders.setText(String.valueOf(todayOrders));
        
        // Format earnings - compact format
        if (todayEarnings >= 1000000) {
            tvTodayEarnings.setText(String.format(Locale.getDefault(), "%.1fM", todayEarnings / 1000000.0));
        } else if (todayEarnings >= 1000) {
            tvTodayEarnings.setText(String.format(Locale.getDefault(), "%.0fK", todayEarnings / 1000.0));
        } else {
            tvTodayEarnings.setText(String.valueOf(todayEarnings));
        }
    }

    private void setupOnlineStatusToggle() {
        switchOnlineStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save online status
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isOnline", isChecked);
            editor.apply();

            // Show toast notification
            String message = isChecked ? "Bạn đang online" : "Bạn đang offline";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMap() {
        // Get the SupportMapFragment and request notification when the map is ready
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // We have our own button
        mMap.getUiSettings().setCompassEnabled(true);
        
        // Check and request location permissions
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void enableMyLocation() {
        if (mMap != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            
            mMap.setMyLocationEnabled(true);
            requestCurrentLocation();
        }
    }

    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tvLocationStatus.setText("Đang lấy vị trí...");
        btnRefreshLocation.setEnabled(false);

        // Try to get last known location first
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateLocationOnMap(location);
                        } else {
                            // Request fresh location
                            requestFreshLocation();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // If last location fails, request fresh location
                    requestFreshLocation();
                });
    }

    private void requestFreshLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Request location updates for a short time to get fresh location
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        
        // Stop location updates after 30 seconds
        btnRefreshLocation.postDelayed(() -> {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            if (tvLocationStatus.getText().equals("Đang lấy vị trí...")) {
                setDefaultLocation();
            }
        }, 30000);
    }

    private void updateLocationOnMap(Location location) {
        if (mMap == null) return;

        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        
        // Remove previous marker
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        
        // Add new marker for current location
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Vị trí hiện tại")
                .snippet("Bạn đang ở đây")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        
        // Move camera to current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
        
        tvLocationStatus.setText("Đã cập nhật");
        btnRefreshLocation.setEnabled(true);
        
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
        
        // Add mock delivery locations
        addMockDeliveryLocations(currentLocation);
    }

    private void setDefaultLocation() {
        if (mMap == null) return;
        
        // Set default location (Ho Chi Minh City)
        LatLng defaultLocation = new LatLng(10.8231, 106.6297);
        
        // Remove previous marker
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        
        // Add marker for default location
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(defaultLocation)
                .title("Vị trí mặc định")
                .snippet("TP. Hồ Chí Minh")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        tvLocationStatus.setText("Vị trí mặc định");
        btnRefreshLocation.setEnabled(true);
        
        // Add mock delivery locations around default location
        addMockDeliveryLocations(defaultLocation);
    }

    private void addMockDeliveryLocations(LatLng currentLocation) {
        // Clear existing delivery markers (keep current location marker)
        mMap.clear();
        
        // Re-add current location marker
        if (currentLocationMarker != null) {
            // Get the previous marker info
            String title = currentLocationMarker.getTitle();
            String snippet = currentLocationMarker.getSnippet();
            
            // Determine marker color based on title
            float markerColor = BitmapDescriptorFactory.HUE_GREEN; // Default for current location
            if (title != null && title.contains("mặc định")) {
                markerColor = BitmapDescriptorFactory.HUE_BLUE;
            }
            
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
        }
        
        // Add some mock nearby delivery locations
        double lat = currentLocation.latitude;
        double lng = currentLocation.longitude;
        
        // Mock delivery points around current location
        LatLng[] deliveryPoints = {
                new LatLng(lat + 0.01, lng + 0.01),
                new LatLng(lat - 0.01, lng + 0.015),
                new LatLng(lat + 0.015, lng - 0.01),
                new LatLng(lat - 0.005, lng - 0.012)
        };
        
        String[] restaurantNames = {
                "KFC Nguyễn Văn Linh",
                "Lotteria Quận 7", 
                "Pizza Hut Phú Mỹ Hưng",
                "McDonald's Crescent Mall"
        };
        
        for (int i = 0; i < deliveryPoints.length; i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(deliveryPoints[i])
                    .title(restaurantNames[i])
                    .snippet("Đơn hàng chờ giao")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                enableMyLocation();
            } else {
                // Permission denied
                tvLocationStatus.setText("Quyền truy cập bị từ chối");
                btnRefreshLocation.setEnabled(true);
                Toast.makeText(getContext(), "Cần quyền truy cập vị trí để hiển thị bản đồ", Toast.LENGTH_LONG).show();
                setDefaultLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (sharedPreferences != null) {
            loadDashboardData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates when fragment is not visible
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
} 