package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.response.AddressResponse;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<AddressResponse> addressList;
    private Context context;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onSetDefaultAddress(String addressId);
        void onDeleteAddress(String addressId);
        void onSelectAddress(String addressId, String addressText);
    }

    public AddressAdapter(Context context, List<AddressResponse> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressResponse address = addressList.get(position);

        // Create a final variable for the address text to avoid lambda issues
        final String addressFull = buildAddressText(address);

        // Set the text
        holder.tvAddress.setText(addressFull);
        CardView cardView = (CardView) holder.itemView;

        if (address.isDefault()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
            holder.borderView.setVisibility(View.VISIBLE);
            holder.tvAddress.setTypeface(Typeface.DEFAULT_BOLD);

            // Add more visual distinction
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.defaultAddressBg));
            cardView.setCardElevation(8f);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
            holder.borderView.setVisibility(View.GONE);
            holder.tvAddress.setTypeface(Typeface.DEFAULT);

            // Reset to default appearance
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            cardView.setCardElevation(4f);
        }

        // Set click listeners
        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefaultAddress(address.getId()+"");
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteAddress(address.getId()+"");
            }
        });

        // Handle item click to return the selected address
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectAddress(address.getId()+"", addressFull);
            }
        });
    }
    private String buildAddressText(AddressResponse address) {
        StringBuilder builder = new StringBuilder();
        if (address.getDetail() != null && !address.getDetail().isEmpty()) {
            builder.append(address.getDetail());
            if (address.getDisplayName() != null && !address.getDisplayName().isEmpty()) {
                builder.append(", ").append(address.getDisplayName());
            }
        } else if (address.getDisplayName() != null) {
            builder.append(address.getDisplayName());
        }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvDefault;
        Button btnSetDefault, btnDelete;
        View borderView;

        AddressViewHolder(View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDefault = itemView.findViewById(R.id.tv_default);
            btnSetDefault = itemView.findViewById(R.id.btn_set_default);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            borderView = itemView.findViewById(R.id.border_view);
        }
    }
}