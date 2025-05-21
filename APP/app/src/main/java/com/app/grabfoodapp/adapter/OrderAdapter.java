package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.activities.PopUpFood;
import com.app.grabfoodapp.apiservice.cart.CartService;
import com.app.grabfoodapp.apiservice.order.OrderService;
import com.app.grabfoodapp.apiservice.review.ReviewService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.ReviewDTO;
import com.app.grabfoodapp.dto.request.CartUpdateRequest;
import com.app.grabfoodapp.dto.response.OrderResponse;
import com.app.grabfoodapp.utils.TokenManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<OrderResponse> orders;
    private TokenManager tokenManager;


    public OrderAdapter(Context context, List<OrderResponse> orders) {
        this.context = context;
        this.orders = orders;
        tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);

        // Định dạng giá
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvOrderShopName.setText(order.getRestaurantName());
        holder.tvOrderTotalPrice.setText(formatter.format(order.getTotalPrice()) + " đ");
        holder.tvOrderShippingFee.setText(formatter.format(order.getShippingFee()) + " đ");

        // Thiết lập RecyclerView lồng cho chi tiết đơn hàng
        holder.rvOrderItem.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderItem.setNestedScrollingEnabled(false); // Tắt cuộn lồng
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(context, order.getCartDetails());
        holder.rvOrderItem.setAdapter(orderDetailAdapter);

        // Hiển thị/ẩn nút Đánh giá
        if (!order.isReview() && "COMPLETED".equals(order.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        if ("COMPLETED".equals(order.getStatus())) {
            holder.btnReorder.setVisibility(View.VISIBLE);
        } else {
            holder.btnReorder.setVisibility(View.GONE);
        }

        holder.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.reviewSection.setVisibility(View.VISIBLE);
                holder.btnReview.setVisibility(View.GONE);
                holder.btnReorder.setVisibility(View.GONE);
            }
        });

        holder.btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderService orderService = ApiClient.getClient().create(OrderService.class);
                long userId = Long.parseLong(tokenManager.getUserId());

                orderService.reorder(userId, order.getId()).enqueue(new Callback<ApiResponse<Boolean>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getData()) {
                                Toast.makeText(context,"Đã thêm lại đơn hàng vào giỏ. Hãy kiểm tra giỏ hàng của bạn!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,"Nhà hàng không còn bán mặt hàng này!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API", "Error response code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                        Log.e("API", "Lỗi mạng hoặc URL: " + t.getMessage());
                    }
                });
            }
        });

        holder.cancelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.reviewSection.setVisibility(View.GONE);
                holder.btnReview.setVisibility(View.VISIBLE);
                holder.btnReorder.setVisibility(View.VISIBLE);
            }
        });

        holder.submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendingReview(
                        order.getId(),
                        BigDecimal.valueOf(holder.ratingBar.getRating()),
                        holder.editTextReviewMessage.getText().toString());
                holder.reviewSection.setVisibility(View.GONE);
                orderDetailAdapter.notifyDataSetChanged();
                holder.btnReorder.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendingReview(long orderId, BigDecimal rating, String review) {
        ReviewDTO.CreateReviewRequest request = ReviewDTO.CreateReviewRequest.builder()
                .orderId(orderId)
                .rating(rating)
                .reviewMessage(review)
                .build();
        ReviewService reviewService = ApiClient.getClient().create(ReviewService.class);
        reviewService.sendReview(request).enqueue(new Callback<ApiResponse<Long>>() {
            @Override
            public void onResponse(Call<ApiResponse<Long>> call, @NonNull Response<ApiResponse<Long>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 202) {
                    Toast.makeText(context, "Review send successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Long>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderShopName, tvOrderTotalPrice, tvOrderShippingFee;
        RecyclerView rvOrderItem;
        Button btnReview;
        Button btnReorder;
        LinearLayout reviewSection;
        RatingBar ratingBar;
        EditText editTextReviewMessage;
        Button cancelReview;
        Button submitReview;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderShopName = itemView.findViewById(R.id.tvOrderShopName);
            tvOrderTotalPrice = itemView.findViewById(R.id.tvOrderTotalPrice);
            tvOrderShippingFee = itemView.findViewById(R.id.tvOrderShippingFee);
            rvOrderItem = itemView.findViewById(R.id.rvOrderItem);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            reviewSection = itemView.findViewById(R.id.reviewSection);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            editTextReviewMessage = itemView.findViewById(R.id.etReview);
            cancelReview = itemView.findViewById(R.id.btnCancel);
            submitReview = itemView.findViewById(R.id.btnSubmit);
        }
    }
}