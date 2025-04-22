package com.api.service;

import com.api.dto.request.AddVoucherDetailRequest;
import com.api.dto.response.VoucherDetailResponse;
import com.api.entity.Food;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherDetailService {
    VoucherDetailResponse addVoucherDetails(AddVoucherDetailRequest request);
    List<VoucherDetail> getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual
            (List<Voucher> voucherList, List<Food> foodList, LocalDateTime currentTime);
}
