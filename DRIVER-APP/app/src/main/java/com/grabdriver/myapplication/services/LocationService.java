package com.grabdriver.myapplication.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.grabdriver.myapplication.MainActivity;
import com.grabdriver.myapplication.R;
import com.grabdriver.myapplication.utils.SessionManager;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LOCATION_SERVICE_CHANNEL";
    private static final int NOTIFICATION_ID = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private SessionManager sessionManager;

    // Location update intervals
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds
    private static final long FASTEST_UPDATE_INTERVAL = 5000; // 5 seconds

    // Interface for location updates
    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
    }

    private static LocationUpdateListener locationUpdateListener;

    public static void setLocationUpdateListener(LocationUpdateListener listener) {
        locationUpdateListener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createNotificationChannel();
        createLocationCallback();

        Log.d(TAG, "LocationService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;

        if ("START_LOCATION_UPDATES".equals(action)) {
            startLocationUpdates();
        } else if ("STOP_LOCATION_UPDATES".equals(action)) {
            stopLocationUpdates();
            stopSelf();
        }

        return START_STICKY; // Restart service if killed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't provide binding
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Tracks your location for delivery purposes");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

                    // Update location in session
                    sessionManager.updateCurrentLocation(location.getLatitude(), location.getLongitude());

                    // Notify listeners
                    if (locationUpdateListener != null) {
                        locationUpdateListener.onLocationUpdate(location);
                    }

                    // TODO: Send location to server
                    sendLocationToServer(location);
                }
            }
        };
    }

    private void startLocationUpdates() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permissions not granted");
            return;
        }

        // Create location request
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(UPDATE_INTERVAL)
                .build();

        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification());

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        Log.d(TAG, "Location updates started");
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "Location updates stopped");
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GrabDriver đang hoạt động")
                .setContentText("Đang theo dõi vị trí để giao hàng")
                .setSmallIcon(R.drawable.ic_delivery_truck)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void sendLocationToServer(Location location) {
        // TODO: Implement API call to send location to server
        // This would typically use Retrofit to send location data
        Log.d(TAG, "Sending location to server: " + location.getLatitude() + ", " + location.getLongitude());

        // Example implementation:
        // LocationUpdateRequest request = new LocationUpdateRequest(
        // sessionManager.getShipperId(),
        // location.getLatitude(),
        // location.getLongitude(),
        // System.currentTimeMillis()
        // );
        // apiService.updateShipperLocation(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        Log.d(TAG, "LocationService destroyed");
    }
}