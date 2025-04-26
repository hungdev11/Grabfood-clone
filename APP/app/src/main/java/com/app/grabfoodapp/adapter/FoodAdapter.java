package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.FoodDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private List<FoodDTO.GetFoodResponse> foods;

    public FoodAdapter(Context context, List<FoodDTO.GetFoodResponse> foods) {
        this.context = context;
        this.foods = foods;
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

        ImageView imageView = convertView.findViewById(R.id.imageViewFood);
        TextView textView = convertView.findViewById(R.id.textViewFoodName);

        textView.setText(food.getName());
        Glide.with(context).load(food.getImage()).into(imageView);

        return convertView;
    }
}

