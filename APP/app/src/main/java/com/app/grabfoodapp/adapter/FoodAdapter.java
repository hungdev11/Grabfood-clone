package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.FoodDTO;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
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

        ImageView foodImg = convertView.findViewById(R.id.imageViewFood);
        TextView foodName = convertView.findViewById(R.id.textViewFoodName);
        TextView foodPrice = convertView.findViewById(R.id.textViewFoodPrice);

        // Hiển thị giá và xử lý khuyến mãi
        handlePriceDisplay(food, foodPrice);

        // Thiết lập tên món ăn và hình ảnh
        foodName.setText(food.getName());
        Glide.with(context).load(food.getImage()).into(foodImg);

        return convertView;
    }

    private void handlePriceDisplay(FoodDTO.GetFoodResponse food, TextView foodPrice) {
        // Sử dụng DecimalFormat để định dạng giá cho đẹp hơn
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        if (food.getPrice().compareTo(food.getDiscountPrice()) == 0) {
            // Hiển thị giá bình thường
            foodPrice.setText(decimalFormat.format(food.getPrice()) + "đ");
        } else {
            // Tạo một chuỗi mới cho giá với khuyến mãi
            String newPrice = decimalFormat.format(food.getDiscountPrice()) + "đ";
            String oldPrice = decimalFormat.format(food.getPrice()) + "đ";

            // Tạo một chuỗi kết hợp cả giá mới và giá cũ
            String combinedText = newPrice + "\n" + oldPrice;

            // Tạo một đối tượng SpannableString để xử lý văn bản
            SpannableString spannableString = new SpannableString(combinedText);

            // Thêm hiệu ứng gạch ngang và màu đỏ cho giá cũ
            int oldPriceStart = newPrice.length() + 1;  // Địa chỉ bắt đầu của giá cũ
            int oldPriceEnd = combinedText.length();  // Địa chỉ kết thúc của giá cũ

            // Gạch ngang cho giá cũ
            spannableString.setSpan(new StrikethroughSpan(), oldPriceStart, oldPriceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Đổi màu giá cũ thành màu đỏ
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), oldPriceStart, oldPriceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Hiển thị chuỗi đã xử lý lên TextView
            foodPrice.setText(spannableString);
        }
    }
}
