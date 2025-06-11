package com.app.grabfoodapp.config;

import android.content.Context;

import com.app.grabfoodapp.adapter.LocalDateTimeAdapter;
import com.app.grabfoodapp.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.1.30:6969/grab/";
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    private static Gson gson = null;

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        }
        return gson;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getHttpClient() {
        if (client == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
        }
        return client;
    }

    // Helper method to format the token for headers
    public static String formatAuthHeader(String token) {
        return "Bearer " + token;
    }

    // Create authenticated service with token
    public static <T> T createServiceWithAuth(Context context, Class<T> serviceClass) {
        TokenManager tokenManager = new TokenManager(context);
        String token = tokenManager.getToken();

        OkHttpClient authenticatedClient = getHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();

                    // Add authorization header with token
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", formatAuthHeader(token))
                            .method(original.method(), original.body());

                    return chain.proceed(requestBuilder.build());
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(authenticatedClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build()
                .create(serviceClass);
    }
}