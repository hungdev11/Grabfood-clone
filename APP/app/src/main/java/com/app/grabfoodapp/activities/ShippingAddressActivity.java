package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.AddressAdapter;
import com.app.grabfoodapp.apiservice.address.AddressService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.LocationDTO;
import com.app.grabfoodapp.dto.response.AddressResponse;
import com.app.grabfoodapp.utils.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingAddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener {

    private RecyclerView recyclerViewAddresses;
    private RecyclerView recyclerViewSuggestions;
    private EditText editTextSearch;

    private AddressAdapter addressAdapter;
    private SuggestionAdapter suggestionAdapter;

    private List<AddressResponse> addressList = new ArrayList<>();
    private List<String> suggestionList = new ArrayList<>();

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Shipping Addresses");
        }

        // Initialize views
        recyclerViewAddresses = findViewById(R.id.recyclerViewAddresses);
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        editTextSearch = findViewById(R.id.editTextSearch);

        // Initialize token manager
        tokenManager = new TokenManager(this);

        // Setup recycler views
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, addressList, this);
        recyclerViewAddresses.setAdapter(addressAdapter);

        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        suggestionAdapter = new SuggestionAdapter();
        recyclerViewSuggestions.setAdapter(suggestionAdapter);

        // Initially hide suggestions
        recyclerViewSuggestions.setVisibility(View.GONE);

        // Setup search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    suggestionList.clear();
                    suggestionAdapter.notifyDataSetChanged();
                    recyclerViewSuggestions.setVisibility(View.GONE);
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

        // Load user's saved addresses
        loadSavedAddresses();
    }

    // Implement interface methods for address actions
    @Override
    public void onSetDefaultAddress(String addressId) {
        setDefaultAddress(addressId);
    }

    @Override
    public void onDeleteAddress(String addressId) {
        confirmDeleteAddress(addressId);
    }

    @Override
    public void onSelectAddress(String addressId, String addressText) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("addressId", addressId);
        resultIntent.putExtra("address", addressText);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedAddresses();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadSavedAddresses() {
        String token = "Bearer " + tokenManager.getToken();
        String userId = tokenManager.getUserId();

        AddressService addressService = ApiClient.getClient().create(AddressService.class);

        // Make API call to get user addresses
        addressService.getUserAddresses(token, userId).enqueue(
                new Callback<List<AddressResponse>>() {
                    @Override
                    public void onResponse(Call<List<AddressResponse>> call,
                                           Response<List<AddressResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            addressList.clear();
                            addressList.addAll(response.body());  // Direct access without getData()
                            addressAdapter = new AddressAdapter(ShippingAddressActivity.this, addressList, ShippingAddressActivity.this);
                            recyclerViewAddresses.setAdapter(addressAdapter);

                            // Show message if no addresses
                            if (addressList.isEmpty()) {
                                Toast.makeText(ShippingAddressActivity.this,
                                        "No saved addresses found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ShippingAddressActivity.this,
                                    "Failed to load addresses: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AddressResponse>> call, Throwable t) {
                        Toast.makeText(ShippingAddressActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
                    suggestionAdapter.notifyDataSetChanged();
                    recyclerViewSuggestions.setVisibility(View.VISIBLE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Error fetching suggestions", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    private String extractProvince(String displayName) {
        if (displayName == null || displayName.isEmpty()) return "";

        String[] parts = displayName.split(",");
        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i].trim();
            if (part.contains("Thành phố") || part.contains("Tỉnh")) {
                return part;
            }
        }
        return "";
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
                    JSONObject result = jsonArray.getJSONObject(0);
                    double lat = result.getDouble("lat");
                    double lon = result.getDouble("lon");

                    // Extract address components from JSON if available
                    JSONObject address = null;
                    if (result.has("address")) {
                        address = result.getJSONObject("address");
                    }
                    String province = extractProvince(result.getString("display_name"));

                    String district = "";
                    String ward = "";

                    StringBuilder detailBuilder = new StringBuilder();
                    if (address.has("house_number")) detailBuilder.append(address.getString("house_number"));
                    if (address.has("road")) {
                        if (detailBuilder.length() > 0) detailBuilder.append(", ");
                        detailBuilder.append(address.getString("road"));
                    }
                    String detail = detailBuilder.toString();

                    if (address != null) {
                        district = address.optString("city", "");
                        ward = address.optString("suburb", "");
                    }

                    // Create LocationDTO with the retrieved values
                    final LocationDTO locationDTO = new LocationDTO(
                            province,                        // province
                            district,                        // district
                            ward,                            // ward
                            detail,                     // detail
                            addressList.isEmpty(),           // isDefault
                            lat,                             // latitude
                            lon                              // longitude
                    );

                    runOnUiThread(() -> {
                        // Clear search and suggestions
                        editTextSearch.setText("");
                        suggestionList.clear();
                        suggestionAdapter.notifyDataSetChanged();
                        recyclerViewSuggestions.setVisibility(View.GONE);

                        // Save to server
                        saveAddressToServer(locationDTO);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Error fetching location details: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveAddressToServer(LocationDTO locationDTO) {
        String token = "Bearer " + tokenManager.getToken();
        String userId = tokenManager.getUserId();

        if (userId == null || token == null) {
            Toast.makeText(this, "Authentication required to save address", Toast.LENGTH_SHORT).show();
            return;
        }

        AddressService addressService = ApiClient.getClient().create(AddressService.class);

        addressService.addUserAddress(token, userId, locationDTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Address saved successfully", Toast.LENGTH_SHORT).show();
                    // Reload the address list
                    loadSavedAddresses();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            try {
                                JSONObject errorJson = new JSONObject(errorBodyStr);
                                String errorMessage = errorJson.optString("message", "Failed to save address");
                                Toast.makeText(ShippingAddressActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(ShippingAddressActivity.this,
                                        "Failed to save address: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        Toast.makeText(ShippingAddressActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultAddress(String addressId) {
        String token = "Bearer " + tokenManager.getToken();
        String userId = tokenManager.getUserId();

        if (userId == null || token == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add a service method to set default address
        AddressService addressService = ApiClient.getClient().create(AddressService.class);

        // Make API call to set default address
        addressService.setDefaultAddress(token, userId, addressId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Default address updated", Toast.LENGTH_SHORT).show();
                    loadSavedAddresses(); // Reload to reflect changes
                } else {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Failed to update default address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteAddress(String addressId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAddress(addressId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAddress(String addressId) {
        String token = "Bearer " + tokenManager.getToken();
        String userId = tokenManager.getUserId();

        if (userId == null || token == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add a service method to delete address
        AddressService addressService = ApiClient.getClient().create(AddressService.class);

        // Make API call to delete address
        addressService.deleteAddress(token, userId, addressId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Address deleted", Toast.LENGTH_SHORT).show();
                    loadSavedAddresses(); // Reload to reflect changes
                } else {
                    Toast.makeText(ShippingAddressActivity.this,
                            "Failed to delete address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ShippingAddressActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter for address suggestions
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