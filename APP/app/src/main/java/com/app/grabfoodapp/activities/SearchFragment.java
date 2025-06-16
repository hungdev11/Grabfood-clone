package com.app.grabfoodapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.app.grabfoodapp.utils.FoodRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private EditText searchEditText;
    private RecyclerView foodsRecyclerView;
    private RecyclerView restaurantsRecyclerView;
    private TextView foodsHeaderText;
    private TextView restaurantsHeaderText;
    private ProgressBar progressBar;
    private View noResultsView;
    private ImageButton btnCameraSearch;
    private LinearLayout imagePreviewContainer;
    private ImageView imgFoodPreview;
    private TextView tvRecognitionResult;

    private FoodSearchAdapter foodAdapter;
    private RestaurantSearchAdapter restaurantAdapter;
    private FoodService foodService;
    private FoodRecognizer foodRecognizer;

    private Timer searchTimer;
    private Long restaurantId = null; // null when searching globally
    private Uri photoUri;

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
        setupImageRecognition();

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
        btnCameraSearch = view.findViewById(R.id.btn_camera_search);
        imagePreviewContainer = view.findViewById(R.id.image_preview_container);
        imgFoodPreview = view.findViewById(R.id.img_food_preview);
        tvRecognitionResult = view.findViewById(R.id.tv_recognition_result);

        // Initially hide results sections
        foodsHeaderText.setVisibility(View.GONE);
        restaurantsHeaderText.setVisibility(View.GONE);
        foodsRecyclerView.setVisibility(View.GONE);
        restaurantsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        noResultsView.setVisibility(View.GONE);
        imagePreviewContainer.setVisibility(View.GONE);
    }

    private void setupRecyclerViews() {
        // Setup foods recycler view
        foodsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodSearchAdapter(new ArrayList<>());
        foodsRecyclerView.setAdapter(foodAdapter);

        // Setup restaurants recycler view
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantAdapter = new RestaurantSearchAdapter(new ArrayList<>());
        restaurantsRecyclerView.setAdapter(restaurantAdapter);
    }

    private void setupApiService() {
        foodService = ApiClient.getClient().create(FoodService.class);
    }

    private void setupImageRecognition() {
        foodRecognizer = new FoodRecognizer(requireContext());

        // Setup camera button click listener
        btnCameraSearch.setOnClickListener(v -> {
            showImageSourceDialog();
        });
    }

    private void showImageSourceDialog() {
        // Create dialog with options for camera or gallery
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn nguồn ảnh")
                .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    if (which == 0) {
                        requestCameraPermission();
                    } else {
                        choosePhotoFromGallery();
                    }
                })
                .show();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Lỗi tạo file ảnh", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        "com.app.grabfoodapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void choosePhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = null;

                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
                } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                    photoUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
                }

                if (bitmap != null) {
                    processImage(bitmap);
                }
            } catch (IOException e) {
                Toast.makeText(getContext(), "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // Camera permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 101) {
            // Storage permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        imagePreviewContainer.setVisibility(View.VISIBLE);
        imgFoodPreview.setImageBitmap(bitmap);
        tvRecognitionResult.setText("Đang nhận diện...");

        // Run recognition in background thread
        new Thread(() -> {
            final String foodName = foodRecognizer.recognizeFood(bitmap);

            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                tvRecognitionResult.setText("Món ăn được nhận diện: " + foodName);

                // Clear text search and perform search with recognized food name
                searchEditText.setText(foodName);
                // The text watcher will trigger the search
            });
        }).start();
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
                }, 500); // 500ms delay
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
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            takePhoto();
        }
    }
//    adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/ten_file.jpg

}