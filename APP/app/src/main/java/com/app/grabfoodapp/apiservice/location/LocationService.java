package com.app.grabfoodapp.apiservice.location;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {
    @GET("location")
    Call<ResponseBody> getLocation(@Query("lat") double lat, @Query("lon") double lon);
}
