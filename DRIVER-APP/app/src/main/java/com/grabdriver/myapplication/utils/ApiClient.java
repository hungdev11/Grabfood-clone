package com.grabdriver.myapplication.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Tạo interceptor để log thông tin request/response
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Tạo session manager để lấy token
            SessionManager sessionManager = new SessionManager(context);

            // Tạo OkHttpClient với interceptor cho authentication
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        
                        // Kiểm tra nếu người dùng đã đăng nhập
                        String token = sessionManager.getToken();
                        if (token != null) {
                            // Xử lý token format: accountId#actualToken -> chỉ lấy actualToken
                            String actualToken = token;
                            if (token.contains("#")) {
                                actualToken = token.substring(token.indexOf("#") + 1);
                            }
                            
                            Request.Builder requestBuilder = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer " + actualToken)
                                    .header("Accept", "application/json")
                                    .method(originalRequest.method(), originalRequest.body());
                            
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                        
                        return chain.proceed(originalRequest);
                    })
                    .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS);

            OkHttpClient httpClient = httpClientBuilder.build();

            // Tạo custom Gson với custom date type adapter - disable default date handling
            CustomDateTypeAdapter dateAdapter = new CustomDateTypeAdapter();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, dateAdapter)
                    .registerTypeAdapter(java.sql.Date.class, dateAdapter)
                    .registerTypeAdapter(java.sql.Timestamp.class, dateAdapter)
                    .setLenient() // Cho phép parse flexible format
                    .create();

            // Tạo Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }
        
        return retrofit;
    }
} 