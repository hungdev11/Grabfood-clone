package com.app.grabfoodapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.app.grabfoodapp.activities.PopUpFood;
import com.app.grabfoodapp.apiservice.cart.CartService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.AdditionFood;
import com.app.grabfoodapp.dto.CartDetailDTO;
import com.app.grabfoodapp.dto.CartResponse;
import com.app.grabfoodapp.dto.request.CartUpdateRequest;
import com.app.grabfoodapp.dto.request.DeleteCartItemRequest;
import com.app.grabfoodapp.utils.TokenManager;
import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    List<CartDetailDTO> cartItems;
    private long userId;
    private long restaurantId;
    private CartService cartService;

    private TokenManager tokenManager;
    private String authHeader;

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        authHeader = "Bearer " + tokenManager.getToken();
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public CartAdapter(List<CartDetailDTO> cartItems) {
        this.cartItems = cartItems;
        cartService = ApiClient.getClient().create(CartService.class);
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartDetailDTO item = cartItems.get(position);

        //Load hinh anh
        Glide.with(holder.itemView.getContext())
                .load(item.getFood_img())
                .into(holder.foodImage);

        holder.foodName.setText(item.getFoodName());

        // Gán danh sách món bổ sung
        StringBuilder additionalFoodsText = new StringBuilder();
        for (int i = 0; i < item.getAdditionFoods().size(); i++) {
            additionalFoodsText.append("+ ").append(item.getAdditionFoods().get(i).getName());
            if (i < item.getAdditionFoods().size() - 1) {
                additionalFoodsText.append("\n");
            }
        }
        holder.additionalFoods.setText(additionalFoodsText.toString());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        // Tính tổng giá (giá món chính + giá món bổ sung)
        BigDecimal totalPrice = item.getPrice();
        for (int i = 0; i < item.getAdditionFoods().size(); i++) {
            totalPrice = totalPrice.add(item.getAdditionFoods().get(i).getPrice());
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        holder.price.setText(formatter.format(totalPrice.multiply(BigDecimal.valueOf(item.getQuantity()))) + "đ");

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    CartDetailDTO currentItem = cartItems.get(currentPosition);
                    int newQuantity = currentItem.getQuantity() + 1;
                    CartUpdateRequest request = CartUpdateRequest.builder()
                            .userId(userId)
                            .cartDetailId(currentItem.getId())
                            .newQuantity(newQuantity)
                            .additionFoodIds(new ArrayList<>())
                            .foodId(0)
                            .build();
                    cartService.updateQuantity(authHeader, request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                currentItem.setQuantity(newQuantity);
                                notifyItemChanged(currentPosition);
                                if (onCartChangeListener != null) {
                                    onCartChangeListener.onCartChanged();  // <- gọi callback
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    CartDetailDTO currentItem = cartItems.get(currentPosition);
                    if (currentItem.getQuantity() <= 1) {
                        return;
                    }
                    int newQuantity = currentItem.getQuantity() - 1;
                    CartUpdateRequest request = CartUpdateRequest.builder()
                            .userId(userId)
                            .cartDetailId(currentItem.getId())
                            .newQuantity(newQuantity)
                            .additionFoodIds(new ArrayList<>())
                            .foodId(0)
                            .build();
                    cartService.updateQuantity(authHeader, request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                currentItem.setQuantity(newQuantity);
                                notifyItemChanged(currentPosition);
                                if (onCartChangeListener != null) {
                                    onCartChangeListener.onCartChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    CartDetailDTO currentItem = cartItems.get(currentPosition);
                    cartService.deleteCartItem(authHeader, currentItem.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                cartItems.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                if (onCartChangeListener != null) {
                                    onCartChangeListener.onCartChanged();  // <- gọi callback
                                }
                            } else {
                                System.out.println("Delete item failed: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Log.e("INFO", "Cart item clicked: " + item.getFoodName());
            Intent intent = new Intent(holder.itemView.getContext(), PopUpFood.class);
            intent.putExtra("selectedCartItem", item);
            intent.putExtra("userId", userId);
            intent.putExtra("restaurantIdFromCart", restaurantId);
            ((Activity) holder.itemView.getContext()).startActivityForResult(intent, 1001);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView additionalFoods;
        TextView quantity;
        TextView price;
        Button increaseButton;
        Button decreaseButton;
        TextView deleteButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            additionalFoods = itemView.findViewById(R.id.additional_foods);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            increaseButton = itemView.findViewById(R.id.increase_quantity);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private OnCartChangeListener onCartChangeListener;

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.onCartChangeListener = listener;
    }

}
