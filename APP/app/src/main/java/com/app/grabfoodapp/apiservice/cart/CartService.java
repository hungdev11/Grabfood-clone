package com.app.grabfoodapp.apiservice.cart;


import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.CartDTO;
import com.app.grabfoodapp.dto.CartResponse;
import com.app.grabfoodapp.dto.request.CartUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CartService {
    @GET("cart/test")
    Call<ApiResponse<CartResponse>> getCart();

    @PUT("cart/update-quantity")
    Call<Void> updateQuantity(@Body CartUpdateRequest request);

    @DELETE("cart")
    Call<Void> deleteCartItem(@Query("cartDetailId") long cartDetailId);
    @POST("cart/add")
    Call<Void> addToCart(
            @Header("Authorization") String bearerToken,
            @Query("userId") Long userId,
            @Body CartDTO.AddToCartRequest request);

    @PUT("cart/update")
    Call<Void> updateWholeItem(
            @Header("Authorization") String bearerToken,
            @Body CartUpdateRequest request);
}
