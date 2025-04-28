package com.app.grabfoodapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.ReviewDTO;

import java.util.List;

public class ReviewHoriAdapter extends RecyclerView.Adapter<ReviewHoriAdapter.ViewHolder> {
    private List<ReviewDTO.ReviewResponse> items;

    public ReviewHoriAdapter(List<ReviewDTO.ReviewResponse> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewMessage;
        public TextView reviewerInfo;
        public ViewHolder(View view) {
            super(view);
            reviewMessage = view.findViewById(R.id.q_review);
            reviewerInfo = view.findViewById(R.id.q_userinfo_rating);
        }
    }

    @Override
    public ReviewHoriAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_res_detail_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewDTO.ReviewResponse currItem = items.get(position);
        holder.reviewMessage.setText(currItem.getReviewMessage());
        holder.reviewerInfo.setText(currItem.getRating() + " * " + currItem.getCustomerName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
