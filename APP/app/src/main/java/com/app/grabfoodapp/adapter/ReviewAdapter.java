package com.app.grabfoodapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.ReviewDTO;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewDTO.ReviewResponse> reviewList = new ArrayList<>();

    public void setReviewList(List<ReviewDTO.ReviewResponse> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDTO.ReviewResponse review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCustomerName, tvOrderString, tvReviewMessage, tvCreatedAt;
        private final TextView tvRating, tvReplyMessage, tvReplyAt;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderString = itemView.findViewById(R.id.tv_order_string);
            tvReviewMessage = itemView.findViewById(R.id.tv_review_message);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvReplyMessage = itemView.findViewById(R.id.tv_reply_message);
            tvReplyAt = itemView.findViewById(R.id.tv_reply_at);
        }

        public void bind(ReviewDTO.ReviewResponse review) {
            tvCustomerName.setText("Khách hàng: " + review.getCustomerName());
            tvOrderString.setText("Đơn hàng: " + review.getOrderString());
            tvReviewMessage.setText(review.getReviewMessage());
            tvCreatedAt.setText("Tạo vào: " + review.getCreatedAt());
            tvRating.setText("Đánh giá: " + review.getRating() + " ★");

            // Show/hide reply message
            if (review.getReplyMessage() != null && !review.getReplyMessage().isEmpty()) {
                tvReplyMessage.setVisibility(View.VISIBLE);
                tvReplyAt.setVisibility(View.VISIBLE);
                tvReplyMessage.setText("Phản hồi: " + review.getReplyMessage());
                tvReplyAt.setText("Vào: " + review.getReplyAt());
            } else {
                tvReplyMessage.setVisibility(View.GONE);
                tvReplyAt.setVisibility(View.GONE);
            }
        }
    }
}

