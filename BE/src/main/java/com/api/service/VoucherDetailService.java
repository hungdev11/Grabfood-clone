package com.api.service;

import com.api.dto.request.AddVoucherDetailRequest;
import com.api.dto.response.VoucherDetailResponse;
import com.api.entity.VoucherDetail;

public interface VoucherDetailService {
    VoucherDetailResponse addVoucherDetails(AddVoucherDetailRequest request);
}
