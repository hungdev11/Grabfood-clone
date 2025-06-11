package com.app.grabfoodapp.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.RestaurantDetailActivity;
import com.app.grabfoodapp.dto.RestaurantDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantSearchAdapter extends RecyclerView.Adapter<RestaurantSearchAdapter.RestaurantViewHolder> {

    private List<RestaurantDTO.RestaurantResponse> restaurants;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(RestaurantDTO.RestaurantResponse restaurant);
    }

    public RestaurantSearchAdapter(List<RestaurantDTO.RestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }

    public void setOnRestaurantClickListener(OnRestaurantClickListener listener) {
        this.listener = listener;
    }

    public void updateRestaurants(List<RestaurantDTO.RestaurantResponse> newRestaurants) {
        this.restaurants = newRestaurants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_search, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        RestaurantDTO.RestaurantResponse restaurant = restaurants.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getAddress());

        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(restaurant.getImage())
                    .placeholder(R.drawable.restaurant_placeholder)
                    .error(R.drawable.restaurant_error)
                    .into(holder.restaurantImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.e("INFO", "Item clicked: " + restaurant.getName());
            // Navigation using Intent
            Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("selectedRestaurant", restaurant);
            holder.itemView.getContext().startActivity(intent);

            // Also call the listener if it exists
            if (listener != null) {
                listener.onRestaurantClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName;
        TextView restaurantAddress;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
        }
    }
}