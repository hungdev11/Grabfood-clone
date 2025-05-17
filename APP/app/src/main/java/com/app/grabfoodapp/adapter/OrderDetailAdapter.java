package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.response.CartDetailResponse;
import com.bumptech.glide.Glide;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<CartDetailResponse> cartItems;
    private Context context;

    public OrderDetailAdapter(Context context, List<CartDetailResponse> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("OrderDetailAdapter", "onBindViewHolder called for position: " + position);
        CartDetailResponse cartItem = cartItems.get(position);

        // Gán dữ liệu cho các view
        holder.foodName.setText(cartItem.getFoodName());
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.price.setText(String.valueOf(cartItem.getPrice()));

        // Xử lý danh sách món bổ sung
        StringBuilder additionalFoodsText = new StringBuilder();
        for (int i = 0; i < cartItem.getAdditionFoods().size(); i++) {
            additionalFoodsText.append("+ ").append(cartItem.getAdditionFoods().get(i).getName());
            if (i < cartItem.getAdditionFoods().size() - 1) {
                additionalFoodsText.append("\n");
            }
        }
        holder.additionalFoods.setText(additionalFoodsText.toString());

        // Tải hình ảnh bằng Glide
        Glide.with(context)
                .load(cartItem.getFood_img()) // Giả sử CartDetailResponse có getImageUrl()
                .into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Lớp ViewHolder để lưu trữ các view
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, additionalFoods, quantity, price;
        Button increaseButton, decreaseButton;

        ViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.order_food_image);
            foodName = itemView.findViewById(R.id.order_food_name);
            additionalFoods = itemView.findViewById(R.id.order_additional_foods);
            quantity = itemView.findViewById(R.id.order_quantity);
            price = itemView.findViewById(R.id.order_price);
            increaseButton = itemView.findViewById(R.id.increase_quantity);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity);
        }
    }
}