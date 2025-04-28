package com.app.grabfoodapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.RestaurantAdapter;
import com.app.grabfoodapp.apiservice.restaurant.RestaurantService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.RestaurantDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ListView listView;
    private RestaurantAdapter adapter;
    private List<RestaurantDTO.RestaurantResponse> restaurantList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home, container, false);

        listView = view.findViewById(R.id.listViewRestaurants); // Phải tìm theo "view"
        adapter = new RestaurantAdapter(getContext(), restaurantList);
        listView.setAdapter(adapter);

        Log.e("INFO", "Fragment Started");

        test();

        return view;
    }

    private void test() {
        RestaurantService restaurantService = ApiClient.getClient().create(RestaurantService.class);

        Call<ApiResponse<List<RestaurantDTO.RestaurantResponse>>> call =
                restaurantService.getRestaurants("name", 13.9747, 108.0117);

        call.enqueue(new Callback<ApiResponse<List<RestaurantDTO.RestaurantResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RestaurantDTO.RestaurantResponse>>> call,
                                   Response<ApiResponse<List<RestaurantDTO.RestaurantResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    restaurantList.clear();
                    restaurantList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    Log.e("INFO", "Data loaded successfully");
                } else {
                    Log.e("API", "Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RestaurantDTO.RestaurantResponse>>> call, Throwable t) {
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }
}
