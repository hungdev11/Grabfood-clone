package com.api.service;

import com.api.dto.request.VoucherRequest;

import com.api.entity.Voucher;
import com.api.dto.response.VoucherResponse;

import java.math.BigDecimal;
import java.util.List;


public interface VoucherService {
    VoucherResponse addVoucher(VoucherRequest request);

    Voucher getVoucherbyId(long id);

    void deleteVoucher(long voucher_id);

    VoucherResponse updateVoucher(long id, VoucherRequest request);

    List<VoucherResponse> getAllVoucher();

    Voucher findVoucherByCode(String code);

    List<VoucherResponse> getVoucherCanApply(BigDecimal totalPrice);
}
