package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.response.VoucherResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class VoucherAdapter extends BaseAdapter {
    private Context context;
    private List<VoucherResponse> vouchers;
    @Getter
    private Map<String, VoucherResponse> selectedVouchersByType;
    private OnVoucherSelectionChangeListener selectionChangeListener;

    public interface OnVoucherSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }

    public VoucherAdapter(Context context, List<VoucherResponse> vouchers, OnVoucherSelectionChangeListener listener) {
        this.context = context;
        this.vouchers = vouchers;
        this.selectedVouchersByType = new HashMap<>();
        this.selectionChangeListener = listener;
    }

    @Override
    public int getCount() {
        return vouchers.size();
    }

    @Override
    public Object getItem(int position) {
        return vouchers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        }

        VoucherResponse response = vouchers.get(position);

        // Set icon dựa trên applyType
        if (response.getApplyType().equals("ORDER")) {
            ImageView imgVoucherIcon = convertView.findViewById(R.id.voucherIcon);
            imgVoucherIcon.setImageResource(R.drawable.voucher_icon_order);
        }

        // Set mã và mô tả voucher
        TextView txtVoucherCode = convertView.findViewById(R.id.voucherTitle);
        txtVoucherCode.setText(response.getCode());

        TextView txtVoucherDescription = convertView.findViewById(R.id.voucherDescription);
        txtVoucherDescription.setText(response.getDescription());

        // Xử lý checkbox
        CheckBox checkBox = convertView.findViewById(R.id.voucherCheckbox);
        checkBox.setOnCheckedChangeListener(null); // Ngăn sự kiện cũ
        checkBox.setChecked(selectedVouchersByType.get(response.getApplyType()) == response);

        // Kiểm tra xem checkbox có nên được vô hiệu hóa không
        boolean isEnabled = !selectedVouchersByType.containsKey(response.getApplyType()) || selectedVouchersByType.get(response.getApplyType()) == response;
        checkBox.setEnabled(isEnabled);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Chọn voucher mới, bỏ chọn voucher cũ cùng type nếu có
                selectedVouchersByType.put(response.getApplyType(), response);
            } else {
                // Bỏ chọn voucher
                if (selectedVouchersByType.get(response.getApplyType()) == response) {
                    selectedVouchersByType.remove(response.getApplyType());
                }
            }
            // Cập nhật giao diện
            notifyDataSetChanged();
            // Thông báo số lượng voucher đã chọn
            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(selectedVouchersByType.size());
            }
        });

        return convertView;
    }

}