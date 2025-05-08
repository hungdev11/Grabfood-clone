package com.app.grabfoodapp.apiservice.voucher;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.response.VoucherResponse;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VoucherService {
    @GET("vouchers/checkApply")
    Call<ApiResponse<List<VoucherResponse>>> getVoucherCanApply(
            @Query("totalPrice")BigDecimal totalPrice);
}
