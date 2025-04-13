package com.api.mapper;

import com.api.dto.request.VoucherRequest;

import com.api.entity.Voucher;
import com.api.dto.response.VoucherResponse;


public interface VoucherMapper {
    Voucher toVoucher(VoucherRequest request);

    VoucherResponse toVoucherResponse(Voucher voucher);
}
