package com.app.grabfoodapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.PopUpFood;
import com.app.grabfoodapp.dto.FoodDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder> {

    private List<FoodDTO.GetFoodResponse> foods;
    private OnFoodClickListener listener;
    private long restaurantId;

    public interface OnFoodClickListener {
        void onFoodClick(FoodDTO.GetFoodResponse food);
    }

    public FoodSearchAdapter(List<FoodDTO.GetFoodResponse> foods, long restaurantId) {
        this.foods = foods;
        this.restaurantId = restaurantId;
    }

    public FoodSearchAdapter(List<FoodDTO.GetFoodResponse> foods) {
        this.foods = foods;
        this.restaurantId = -1;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void updateFoods(List<FoodDTO.GetFoodResponse> newFoods) {
        this.foods = newFoods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_search, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodDTO.GetFoodResponse food = foods.get(position);
        holder.foodName.setText(food.getName());
        holder.foodPrice.setText(String.format("%d VNĐ", food.getPrice().intValue()));

        if (food.getImage() != null && !food.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(food.getImage())
                    .placeholder(R.drawable.food_placeholder)
                    .error(R.drawable.food_error)
                    .into(holder.foodImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PopUpFood.class);
            intent.putExtra("selectedFood", food);
            intent.putExtra("restaurantId", food.getRestaurantId()); // Assuming this field exists
            holder.itemView.getContext().startActivity(intent);

            // Also call the listener if needed
            if (listener != null) {
                listener.onFoodClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView foodPrice;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodPrice = itemView.findViewById(R.id.food_price);
        }
    }
}