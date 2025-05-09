package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.utils.TokenManager;

public class ProfileFragment extends Fragment {
    private Button btnLogout;
    private TokenManager tokenManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        tokenManager = new TokenManager(requireContext());
        btnLogout = view.findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(v -> performLogout());

        CardView personalInfoCard = view.findViewById(R.id.personal_info_card);
        CardView shippingAddressCard = view.findViewById(R.id.shipping_address_card);
        CardView myOrdersCard = view.findViewById(R.id.my_orders_card);

        // Set click listeners
        personalInfoCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
            startActivity(intent);
        });

        shippingAddressCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ShippingAddressActivity.class);
            startActivity(intent);
        });

        myOrdersCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        });

        return view;
    }
    private void performLogout() {
        // Clear token
        tokenManager.logout();

        // Show success message
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}