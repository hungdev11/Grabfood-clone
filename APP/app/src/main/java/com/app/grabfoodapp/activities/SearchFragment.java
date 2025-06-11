package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.FoodSearchAdapter;
import com.app.grabfoodapp.adapter.RestaurantSearchAdapter;
import com.app.grabfoodapp.apiservice.food.FoodService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.FoodDTO;
import com.app.grabfoodapp.dto.RestaurantDTO;
import com.app.grabfoodapp.dto.response.SearchResultResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView foodsRecyclerView;
    private RecyclerView restaurantsRecyclerView;
    private TextView foodsHeaderText;
    private TextView restaurantsHeaderText;
    private ProgressBar progressBar;
    private View noResultsView;

    private FoodSearchAdapter foodAdapter;
    private RestaurantSearchAdapter restaurantAdapter;
    private FoodService foodService;

    private Timer searchTimer;
    private Long restaurantId = null; // null when searching globally

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        initViews(view);
        setupRecyclerViews();
        setupSearchListener();
        setupApiService();

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.search_edit_text);
        foodsRecyclerView = view.findViewById(R.id.foods_recycler_view);
        restaurantsRecyclerView = view.findViewById(R.id.restaurants_recycler_view);
        foodsHeaderText = view.findViewById(R.id.foods_header);
        restaurantsHeaderText = view.findViewById(R.id.restaurants_header);
        progressBar = view.findViewById(R.id.search_progress_bar);
        noResultsView = view.findViewById(R.id.no_results_view);

        // Initially hide results sections
        foodsHeaderText.setVisibility(View.GONE);
        restaurantsHeaderText.setVisibility(View.GONE);
        foodsRecyclerView.setVisibility(View.GONE);
        restaurantsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        noResultsView.setVisibility(View.GONE);
    }

    private void setupRecyclerViews() {
        // Setup foods recycler view
        foodsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodSearchAdapter(new ArrayList<>());
//        foodAdapter.setOnFoodClickListener(food -> addFoodToCart(food));
        foodsRecyclerView.setAdapter(foodAdapter);

        // Setup restaurants recycler view
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantAdapter = new RestaurantSearchAdapter(new ArrayList<>());
        restaurantsRecyclerView.setAdapter(restaurantAdapter);


    }
//    private void addFoodToCart(FoodDTO.GetFoodResponse food) {
//        // Implement cart functionality
//        CartManager.getInstance().addFoodToCart(requireContext(), food);
//        Toast.makeText(getContext(), "Đã thêm " + food.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
//    }


    private void setupApiService() {
        foodService = ApiClient.getClient().create(FoodService.class);
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel any pending searches
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();

                if (query.isEmpty()) {
                    clearResults();
                    return;
                }

                // Delay the search to avoid excessive API calls while typing
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> performSearch(query));
                        }
                    }
                }, 300); // 300ms delay
            }
        });
    }

    private void performSearch(String query) {
        progressBar.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);

        Call<ApiResponse<SearchResultResponse>> call = foodService.searchFoodsAndRestaurants(
                query, restaurantId, true);

        call.enqueue(new Callback<ApiResponse<SearchResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchResultResponse>> call, Response<ApiResponse<SearchResultResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body().getData()!= null) {
                    updateResults(response.body());
                } else {
                    Toast.makeText(getContext(), "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show();
                    clearResults();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchResultResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                clearResults();
            }
        });
    }

    private void updateResults(ApiResponse<SearchResultResponse> data) {
        boolean hasResults = false;

        // Update foods list
        List<FoodDTO.GetFoodResponse> foods = data.getData().getFoods();
        if (foods != null && !foods.isEmpty()) {
            foodsHeaderText.setVisibility(View.VISIBLE);
            foodsRecyclerView.setVisibility(View.VISIBLE);
            foodAdapter.updateFoods(foods);
            hasResults = true;
        } else {
            foodsHeaderText.setVisibility(View.GONE);
            foodsRecyclerView.setVisibility(View.GONE);
        }

        // Update restaurants list (only if not searching within a restaurant)
        List<RestaurantDTO.RestaurantResponse> restaurants = data.getData().getRestaurants();
        if (restaurantId == null && restaurants != null && !restaurants.isEmpty()) {
            restaurantsHeaderText.setVisibility(View.VISIBLE);
            restaurantsRecyclerView.setVisibility(View.VISIBLE);
            restaurantAdapter.updateRestaurants(restaurants);
            hasResults = true;
        } else {
            restaurantsHeaderText.setVisibility(View.GONE);
            restaurantsRecyclerView.setVisibility(View.GONE);
        }

        // Show no results message if needed
        noResultsView.setVisibility(hasResults ? View.GONE : View.VISIBLE);
    }

    private void clearResults() {
        foodsHeaderText.setVisibility(View.GONE);
        restaurantsHeaderText.setVisibility(View.GONE);
        foodsRecyclerView.setVisibility(View.GONE);
        restaurantsRecyclerView.setVisibility(View.GONE);
        noResultsView.setVisibility(View.GONE);

        if (foodAdapter != null) {
            foodAdapter.updateFoods(new ArrayList<>());
        }
        if (restaurantAdapter != null) {
            restaurantAdapter.updateRestaurants(new ArrayList<>());
        }
    }

    // Method to set restaurant ID when searching within a restaurant
    public void setRestaurantId(Long id) {
        this.restaurantId = id;
        // Clear previous search when context changes
        searchEditText.setText("");
    }
}
