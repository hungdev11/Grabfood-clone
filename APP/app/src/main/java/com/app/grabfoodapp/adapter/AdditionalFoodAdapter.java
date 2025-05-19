package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.PopUpFood;
import com.app.grabfoodapp.dto.FoodDTO;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdditionalFoodAdapter extends BaseAdapter {
    private Context context;
    private List<FoodDTO.GetFoodResponse> foods;
    private Set<Long> ids = new HashSet<>();

    public AdditionalFoodAdapter(Context context, List<FoodDTO.GetFoodResponse> foods) {
        this.context = context;
        this.foods = foods;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> newIds) {
        this.ids = newIds;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodDTO.GetFoodResponse food = foods.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_additional_food_popup, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkbox_addi);
        TextView foodName = convertView.findViewById(R.id.tv_addi_food_name);
        TextView foodPrice = convertView.findViewById(R.id.tv_addi_food_price);

        foodName.setText(food.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        BigDecimal price = food.getDiscountPrice();
        if (price != null) {
            foodPrice.setText(decimalFormat.format(price) + "đ");
        } else {
            foodPrice.setText("0đ");
        }

        checkBox.setOnCheckedChangeListener(null); // clear listener cũ trước
        checkBox.setChecked(ids.contains(food.getId())); // set checked đúng theo ids

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean b = isChecked ? ids.add(food.getId()) : ids.remove(food.getId());
            if (context instanceof PopUpFood) {
                ((PopUpFood) context).updateTotalPrice();
            }
        });

        return convertView;
    }

    // Fix for AdditionalFoodAdapter.java
    public BigDecimal getTotalSelectedPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Long id : ids) {
            for (FoodDTO.GetFoodResponse food : foods) {
                if (food.getId()==id) {
                    // FIX: Check for null discount price before adding
                    BigDecimal price = food.getDiscountPrice();
                    if (price != null) {
                        total = total.add(price);
                    }
                    break;
                }
            }
        }
        return total;
    }

}
