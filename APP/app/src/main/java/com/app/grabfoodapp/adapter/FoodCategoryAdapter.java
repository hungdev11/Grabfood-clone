package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.FoodDTO;

import java.util.List;
import java.util.Map;

public class FoodCategoryAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> categories;
    private Map<String, List<FoodDTO.GetFoodResponse>> categoryFoodsMap;
    private final long restaurantId;

    public FoodCategoryAdapter(Context context, List<String> categories, Map<String, List<FoodDTO.GetFoodResponse>> categoryFoodsMap, long restaurantId) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
        this.categoryFoodsMap = categoryFoodsMap;
        this.restaurantId = restaurantId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String category = categories.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category_with_grid, parent, false);
        }

        TextView categoryName = convertView.findViewById(R.id.textViewCategoryName);
        GridView gridView = convertView.findViewById(R.id.gridViewFoods);

        categoryName.setText(category);

        List<FoodDTO.GetFoodResponse> foods = categoryFoodsMap.get(category);
        FoodAdapter foodAdapter = new FoodAdapter(context, foods, restaurantId);
        gridView.setAdapter(foodAdapter);

        return convertView;
    }
}

