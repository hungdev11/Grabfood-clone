package com.app.grabfoodapp.adapter;

import static com.app.grabfoodapp.utils.Util.handlePriceDisplay;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.PopUpFood;
import com.app.grabfoodapp.dto.FoodDTO;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private List<FoodDTO.GetFoodResponse> foods;

    private final long restaurantId;

    public FoodAdapter(Context context, List<FoodDTO.GetFoodResponse> foods, long restaurantId) {
        this.context = context;
        this.foods = foods;
        this.restaurantId = restaurantId;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return foods.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodDTO.GetFoodResponse food = foods.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food_res, parent, false);
        }

        ImageView foodImg = convertView.findViewById(R.id.imageViewFood);
        TextView foodName = convertView.findViewById(R.id.textViewFoodName);
        TextView foodPrice = convertView.findViewById(R.id.textViewFoodPrice);

        // Hiển thị giá và xử lý khuyến mãi
        handlePriceDisplay(food, foodPrice);

        // Thiết lập tên món ăn và hình ảnh
        foodName.setText(food.getName());
        Glide.with(context).load(food.getImage()).into(foodImg);

        convertView.setOnClickListener(v -> {
            Log.e("INFO", "Item clicked: " + food.getName());
            Intent intent = new Intent(context, PopUpFood.class);
            intent.putExtra("selectedFood", food);
            intent.putExtra("restaurantId", restaurantId);
            context.startActivity(intent);
        });

        return convertView;
    }
}
