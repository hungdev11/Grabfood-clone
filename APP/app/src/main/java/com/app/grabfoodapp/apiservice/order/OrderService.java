package com.app.grabfoodapp.apiservice.order;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.request.ApplyVoucherRequest;
import com.app.grabfoodapp.dto.response.ApplyVoucherResponse;
import com.app.grabfoodapp.dto.response.CheckDistanceResponse;
import com.app.grabfoodapp.dto.response.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    @POST("order/check/applyVoucher")
    Call<ApiResponse<ApplyVoucherResponse>> checkApplyVoucher(@Body ApplyVoucherRequest request);

    @GET("order")
    Call<List<OrderResponse>> getAllOrders(@Header("Authorization") String authHeader);

    @POST("order/user/{userId}/reorder/{orderId}")
    Call<ApiResponse<Boolean>> reorder(@Path("userId") long userId, @Path("orderId") long orderId);

    @GET("order/checkDistance")
    Call<ApiResponse<CheckDistanceResponse>> checkDistance(@Query("userId") long userId, @Query("lat") double lat, @Query("lon") double lon);

}
