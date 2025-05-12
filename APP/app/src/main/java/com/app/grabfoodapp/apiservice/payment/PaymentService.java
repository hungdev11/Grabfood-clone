package com.app.grabfoodapp.apiservice.payment;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.request.CreateOrderRequest;
import com.app.grabfoodapp.dto.response.OrderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentService {
    @POST("payments/cod")
    Call<ApiResponse<OrderResponse>> createCodOrder(@Header("Authorization") String authHeader, @Body CreateOrderRequest request);

}
