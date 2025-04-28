package com.app.grabfoodapp.apiservice.cart;

import com.app.grabfoodapp.dto.CartDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CartService {
    @POST("cart/add")
    Call<Void> addToCart(
            @Header("Authorization") String bearerToken,
            @Query("userId") Long userId,
            @Body CartDTO.AddToCartRequest request);
}
