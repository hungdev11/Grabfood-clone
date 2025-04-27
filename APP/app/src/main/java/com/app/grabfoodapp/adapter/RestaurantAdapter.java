package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.RestaurantDetailActivity;
import com.app.grabfoodapp.dto.RestaurantDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<RestaurantDTO.RestaurantResponse> {

    public RestaurantAdapter(Context context, List<RestaurantDTO.RestaurantResponse> restaurants) {
        super(context, 0, restaurants);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RestaurantDTO.RestaurantResponse restaurant = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurant, parent, false);
        }

        TextView resName = convertView.findViewById(R.id.home_restaurant_name);
        ImageView resImg = convertView.findViewById(R.id.home_restaurant_image);
        TextView resRating = convertView.findViewById(R.id.home_restaurant_rating);
        TextView resShippingFee = convertView.findViewById(R.id.home_restaurant_shipping_fee);
        TextView resTimeDistance = convertView.findViewById(R.id.home_restaurant_time_distance);
        RecyclerView recyclerView = convertView.findViewById(R.id.home_res_vouchers_info);

        resName.setText(restaurant.getName());
        resRating.setText(restaurant.getRating().toString());
        resShippingFee.setText("8000đ");
        resTimeDistance.setText("Cách " + restaurant.getTimeDistance());

        Glide.with(getContext())
                .load(restaurant.getImage())
                .into(resImg);

        // Tạo LayoutManager cuộn ngang
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Adapter để đổ dữ liệu voucher
        RestaurantVouchersInfoAdapter adapter = new RestaurantVouchersInfoAdapter(restaurant.getRestaurantVouchersInfo());
        recyclerView.setAdapter(adapter);

        convertView.setOnClickListener(v -> {
            Log.e("INFO", "Item clicked: " + restaurant.getName());
            // Thực hiện hành động khi nhấn vào item, ví dụ như mở chi tiết nhà hàng
            Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
            intent.putExtra("selectedRestaurant", restaurant);
            getContext().startActivity(intent);
        });
        return convertView;
    }
}
