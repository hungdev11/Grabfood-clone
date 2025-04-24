package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.RestaurantDTO;

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

        TextView tvName = convertView.findViewById(R.id.tvRestaurantName);
        tvName.setText(restaurant.getName());

        return convertView;
    }
}
