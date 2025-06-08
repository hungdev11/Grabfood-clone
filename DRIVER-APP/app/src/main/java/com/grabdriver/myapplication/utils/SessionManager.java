package com.grabdriver.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import com.grabdriver.myapplication.models.Shipper;

public class SessionManager {
    private static final String PREF_NAME = "GrabDriverSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_SHIPPER_ID = "shipperId";
    private static final String KEY_SHIPPER_NAME = "shipperName";
    private static final String KEY_SHIPPER_PHONE = "shipperPhone";
    private static final String KEY_SHIPPER_EMAIL = "shipperEmail";
    private static final String KEY_SHIPPER_RATING = "shipperRating";
    private static final String KEY_SHIPPER_STATUS = "shipperStatus";
    private static final String KEY_SHIPPER_VEHICLE_TYPE = "shipperVehicleType";
    private static final String KEY_SHIPPER_LICENSE_PLATE = "shipperLicensePlate";
    private static final String KEY_SHIPPER_GEMS = "shipperGems";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    // New keys for enhanced features
    private static final String KEY_IS_ONLINE = "isOnline";
    private static final String KEY_CURRENT_LATITUDE = "currentLatitude";
    private static final String KEY_CURRENT_LONGITUDE = "currentLongitude";
    private static final String KEY_LAST_LOCATION_UPDATE = "lastLocationUpdate";
    private static final String KEY_FCM_TOKEN = "fcmToken";
    private static final String KEY_OFFLINE_MODE = "offlineMode";
    private static final String KEY_LOCATION_TRACKING_ENABLED = "locationTrackingEnabled";
    private static final String KEY_SESSION_START_TIME = "sessionStartTime";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session with shipper data
     */
    public void createLoginSession(String token, Shipper shipper, boolean rememberMe) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_SHIPPER_ID, shipper.getId());
        editor.putString(KEY_SHIPPER_NAME, shipper.getName());
        editor.putString(KEY_SHIPPER_PHONE, shipper.getPhone());
        editor.putString(KEY_SHIPPER_EMAIL, shipper.getEmail());
        editor.putFloat(KEY_SHIPPER_RATING, (float) shipper.getRating());
        editor.putString(KEY_SHIPPER_STATUS, shipper.getStatus());
        editor.putString(KEY_SHIPPER_VEHICLE_TYPE, shipper.getVehicleType());
        editor.putString(KEY_SHIPPER_LICENSE_PLATE, shipper.getLicensePlate());
        editor.putInt(KEY_SHIPPER_GEMS, shipper.getGems());
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.putLong(KEY_SESSION_START_TIME, System.currentTimeMillis());
        editor.putBoolean(KEY_IS_ONLINE, shipper.isOnline());
        editor.putBoolean(KEY_LOCATION_TRACKING_ENABLED, true);
        editor.commit();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Check if user wants to be remembered
     */
    public boolean isRememberMeEnabled() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    /**
     * Get authentication token
     */
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    /**
     * Get logged in shipper ID
     */
    public Long getShipperId() {
        long id = pref.getLong(KEY_SHIPPER_ID, -1);
        return id != -1 ? id : null;
    }

    /**
     * Get logged in shipper data
     */
    public Shipper getShipperInfo() {
        if (!isLoggedIn()) {
            return null;
        }

        Shipper shipper = new Shipper();
        shipper.setId(getShipperId());
        shipper.setName(pref.getString(KEY_SHIPPER_NAME, ""));
        shipper.setPhone(pref.getString(KEY_SHIPPER_PHONE, ""));
        shipper.setEmail(pref.getString(KEY_SHIPPER_EMAIL, ""));
        shipper.setRating(pref.getFloat(KEY_SHIPPER_RATING, 0.0f));
        shipper.setStatus(pref.getString(KEY_SHIPPER_STATUS, ""));
        shipper.setVehicleType(pref.getString(KEY_SHIPPER_VEHICLE_TYPE, ""));
        shipper.setLicensePlate(pref.getString(KEY_SHIPPER_LICENSE_PLATE, ""));
        shipper.setGems(pref.getInt(KEY_SHIPPER_GEMS, 0));
        shipper.setOnline(pref.getBoolean(KEY_IS_ONLINE, false));

        return shipper;
    }

    /**
     * Update shipper information in session
     */
    public void updateShipperInfo(Shipper shipper) {
        if (isLoggedIn()) {
            editor.putString(KEY_SHIPPER_NAME, shipper.getName());
            editor.putString(KEY_SHIPPER_EMAIL, shipper.getEmail());
            editor.putFloat(KEY_SHIPPER_RATING, (float) shipper.getRating());
            editor.putString(KEY_SHIPPER_STATUS, shipper.getStatus());
            editor.putString(KEY_SHIPPER_VEHICLE_TYPE, shipper.getVehicleType());
            editor.putString(KEY_SHIPPER_LICENSE_PLATE, shipper.getLicensePlate());
            editor.putInt(KEY_SHIPPER_GEMS, shipper.getGems());
            editor.putBoolean(KEY_IS_ONLINE, shipper.isOnline());
            editor.commit();
        }
    }

    /**
     * Update online status
     */
    public void setOnlineStatus(boolean isOnline) {
        if (isLoggedIn()) {
            editor.putBoolean(KEY_IS_ONLINE, isOnline);
            editor.commit();
        }
    }

    /**
     * Get online status
     */
    public boolean isOnline() {
        return pref.getBoolean(KEY_IS_ONLINE, false);
    }

    /**
     * Update current location
     */
    public void updateCurrentLocation(double latitude, double longitude) {
        if (isLoggedIn()) {
            editor.putFloat(KEY_CURRENT_LATITUDE, (float) latitude);
            editor.putFloat(KEY_CURRENT_LONGITUDE, (float) longitude);
            editor.putLong(KEY_LAST_LOCATION_UPDATE, System.currentTimeMillis());
            editor.commit();
        }
    }

    /**
     * Get current latitude
     */
    public double getCurrentLatitude() {
        return pref.getFloat(KEY_CURRENT_LATITUDE, 0.0f);
    }

    /**
     * Get current longitude
     */
    public double getCurrentLongitude() {
        return pref.getFloat(KEY_CURRENT_LONGITUDE, 0.0f);
    }

    /**
     * Get last location update time
     */
    public long getLastLocationUpdateTime() {
        return pref.getLong(KEY_LAST_LOCATION_UPDATE, 0);
    }

    /**
     * Save FCM token
     */
    public void saveFCMToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.commit();
    }

    /**
     * Get FCM token
     */
    public String getFCMToken() {
        return pref.getString(KEY_FCM_TOKEN, null);
    }

    /**
     * Enable/disable offline mode
     */
    public void setOfflineMode(boolean enabled) {
        editor.putBoolean(KEY_OFFLINE_MODE, enabled);
        editor.commit();
    }

    /**
     * Check if offline mode is enabled
     */
    public boolean isOfflineModeEnabled() {
        return pref.getBoolean(KEY_OFFLINE_MODE, false);
    }

    /**
     * Enable/disable location tracking
     */
    public void setLocationTrackingEnabled(boolean enabled) {
        editor.putBoolean(KEY_LOCATION_TRACKING_ENABLED, enabled);
        editor.commit();
    }

    /**
     * Check if location tracking is enabled
     */
    public boolean isLocationTrackingEnabled() {
        return pref.getBoolean(KEY_LOCATION_TRACKING_ENABLED, true);
    }

    /**
     * Get session start time
     */
    public long getSessionStartTime() {
        return pref.getLong(KEY_SESSION_START_TIME, System.currentTimeMillis());
    }

    /**
     * Check if location data is recent (within last 5 minutes)
     */
    public boolean isLocationDataRecent() {
        long lastUpdate = getLastLocationUpdateTime();
        long currentTime = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000; // 5 minutes in milliseconds
        return (currentTime - lastUpdate) < fiveMinutes;
    }

    /**
     * Clear session data and logout
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }

    /**
     * Get shipper name for display
     */
    public String getShipperName() {
        return pref.getString(KEY_SHIPPER_NAME, "Driver");
    }

    /**
     * Get shipper phone
     */
    public String getShipperPhone() {
        return pref.getString(KEY_SHIPPER_PHONE, "");
    }

    /**
     * Check if session is valid (has token and shipper ID)
     */
    public boolean isSessionValid() {
        return isLoggedIn() &&
                getToken() != null &&
                !getToken().isEmpty() &&
                getShipperId() != null;
    }

    /**
     * Check if session is expired (more than 24 hours)
     */
    public boolean isSessionExpired() {
        long sessionStart = getSessionStartTime();
        long currentTime = System.currentTimeMillis();
        long twentyFourHours = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        return (currentTime - sessionStart) > twentyFourHours;
    }

    /**
     * Get current location as Location object
     */
    public Location getCurrentLocation() {
        if (!isLoggedIn() || !isLocationTrackingEnabled()) {
            return null;
        }
        
        double lat = getCurrentLatitude();
        double lon = getCurrentLongitude();
        
        // Return null if no location data available
        if (lat == 0.0 && lon == 0.0) {
            return null;
        }
        
        Location location = new Location("session");
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }
}