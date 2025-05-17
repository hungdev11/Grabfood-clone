package com.app.grabfoodapp.apiservice.order;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.request.ApplyVoucherRequest;
import com.app.grabfoodapp.dto.response.ApplyVoucherResponse;
import com.app.grabfoodapp.dto.response.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OrderService {
    @POST("order/check/applyVoucher")
    Call<ApiResponse<ApplyVoucherResponse>> checkApplyVoucher(@Body ApplyVoucherRequest request);

    @GET("order")
    Call<List<OrderResponse>> getAllOrders(@Header("Authorization") String authHeader);
}
