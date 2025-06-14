package com.app.grabfoodapp.apiservice.auth;

import com.app.grabfoodapp.dto.LoginResponse;
import com.app.grabfoodapp.dto.UserInfoResponse;
import com.app.grabfoodapp.dto.request.ChangePasswordRequest;
import com.app.grabfoodapp.dto.request.LoginRequest;
import com.app.grabfoodapp.dto.request.RegisterRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthService {
    @POST("auth/generateToken2")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/addNewAccount2")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);
    @GET("auth/user/me")
    Call<UserInfoResponse> getUserInfo(@Header("Authorization") String token);
    @PUT("auth/user/change-password")
    Call<Void> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
}
