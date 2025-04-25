package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.grabfoodapp.R;
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

        resName.setText(restaurant.getName());
        resRating.setText(restaurant.getRating().toString());
        resShippingFee.setText("8000đ");
        resTimeDistance.setText("From 25 mins");

        Glide.with(getContext())
                .load(restaurant.getImage()) // URL từ server
//                .placeholder(R.drawable.placeholder) // ảnh tạm khi đang tải
//                .error(R.drawable.error_image) // ảnh khi tải lỗi
                .into(resImg);
        return convertView;
    }
}
