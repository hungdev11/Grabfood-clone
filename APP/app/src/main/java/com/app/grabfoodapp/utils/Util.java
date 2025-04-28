package com.app.grabfoodapp.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import com.app.grabfoodapp.dto.FoodDTO;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Util {
    public static void handlePriceDisplay(FoodDTO.GetFoodResponse food, TextView foodPrice) {
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
