package com.app.grabfoodapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.utils.LocationStorage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button btnUseCurrentLocation, btnConfirmLocation;
    private EditText editTextAddress;
    private RecyclerView recyclerViewSuggestions;
    private WebView mapWebView;

    private boolean webViewReady = false;
    private SuggestionAdapter adapter;
    private List<String> suggestionList = new ArrayList<>();
    private boolean isAddressSelected = false;

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnUseCurrentLocation = findViewById(R.id.btnUseCurrentLocation);
        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);
        editTextAddress = findViewById(R.id.editTextAddress);
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        mapWebView = findViewById(R.id.mapWebView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupWebView();

        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestionAdapter();
        recyclerViewSuggestions.setAdapter(adapter);

        btnConfirmLocation.setEnabled(false);
        btnConfirmLocation.setVisibility(View.GONE);

        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isAddressSelected) {
                    isAddressSelected = false;
                    btnConfirmLocation.setEnabled(false);
                    btnConfirmLocation.setVisibility(View.GONE);
                }

                if (s.length() == 0) {
                    suggestionList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }

                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        fetchAddressSuggestions(s.toString());
                    }
                }, DELAY);
            }
        });

        btnUseCurrentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCurrentLocation();
            }
        });

        btnConfirmLocation.setOnClickListener(v -> {
            saveLocationAndGoToMain(LocationStorage.getLatitude(this), LocationStorage.getLongitude(this));
        });
    }

    private void setupWebView() {
        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mapWebView.evaluateJavascript("isMapReady", value -> {
                    if ("true".equals(value)) {
                        webViewReady = true;
                    }
                });
            }
        });

        mapWebView.setWebChromeClient(new WebChromeClient());
        mapWebView.getSettings().setJavaScriptEnabled(true);
        mapWebView.loadUrl("file:///android_asset/leaflet_map.html");
    }

    private void fetchAddressSuggestions(String query) {
        if (query.isEmpty()) return;

        new Thread(() -> {
            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(query, "UTF-8")
                        + "&format=json&addressdetails=1&limit=5";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "GrabFoodApp");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                List<String> results = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String displayName = jsonArray.getJSONObject(i).getString("display_name");
                    results.add(displayName);
                }

                runOnUiThread(() -> {
                    suggestionList.clear();
                    suggestionList.addAll(results);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchLatLonFromDisplayName(String displayName) {
        new Thread(() -> {
            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(displayName, "UTF-8")
                        + "&format=json&addressdetails=1&limit=1";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "GrabFoodApp");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                if (jsonArray.length() > 0) {
                    double lat = jsonArray.getJSONObject(0).getDouble("lat");
                    double lon = jsonArray.getJSONObject(0).getDouble("lon");
                    runOnUiThread(() -> {
                        LocationStorage.saveLocation(this, lat, lon);
                        updateMapLocation(lat, lon);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateMapLocation(double lat, double lon) {
        if (webViewReady) {
            String js = "updateMap(" + lat + "," + lon + ");";
            mapWebView.evaluateJavascript(js, null);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        saveLocationAndGoToMain(latitude, longitude);
                        //updateMapLocation(latitude, longitude);
                    }
                });
    }

    private void saveLocationAndGoToMain(double lat, double lon) {
        LocationStorage.saveLocation(this, lat, lon);

        Intent intent = new Intent(LocationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = suggestionList.get(position);
            holder.textView.setText(item);
            holder.itemView.setOnClickListener(v -> {
                editTextAddress.setText(item);
                suggestionList.clear();
                notifyDataSetChanged();
                isAddressSelected = true;
                btnConfirmLocation.setEnabled(true);
                btnConfirmLocation.setVisibility(View.VISIBLE);
                fetchLatLonFromDisplayName(item);
            });
        }

        @Override
        public int getItemCount() {
            return suggestionList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
