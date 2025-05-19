package com.app.grabfoodapp.apiservice.address;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.LocationDTO;
import com.app.grabfoodapp.dto.response.AddressResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressService {
    @POST("users/{userId}/addresses")
    Call<ResponseBody> addUserAddress(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body LocationDTO locationDTO);

    @GET("users/{userId}/addresses")
    Call<List<AddressResponse>> getUserAddresses(
            @Header("Authorization") String token,
            @Path("userId") String userId);
    @PATCH("users/{userId}/addresses/{addressId}/default")
    Call<ResponseBody> setDefaultAddress(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Path("addressId") String addressId);

    @DELETE("users/{userId}/addresses/{addressId}")
    Call<ResponseBody> deleteAddress(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Path("addressId") String addressId);
}
