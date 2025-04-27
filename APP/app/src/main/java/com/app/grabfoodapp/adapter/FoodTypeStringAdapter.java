package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.grabfoodapp.R;
import java.util.List;

public class FoodTypeStringAdapter extends RecyclerView.Adapter<FoodTypeStringAdapter.FoodTypeViewHolder> {

    private Context context;
    private List<String> foodTypes;
    private OnItemClickListener listener;

    public FoodTypeStringAdapter(Context context, List<String> foodTypes) {
        this.context = context;
        this.foodTypes = foodTypes;
    }

    @NonNull
    @Override
    public FoodTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_type_string, parent, false);
        return new FoodTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodTypeViewHolder holder, int position) {
        String foodType = foodTypes.get(position);
        holder.txtFoodType.setText(foodType);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(foodType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodTypes.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FoodTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodType;

        public FoodTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodType = itemView.findViewById(R.id.txtFoodType);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String foodType);
    }
}
