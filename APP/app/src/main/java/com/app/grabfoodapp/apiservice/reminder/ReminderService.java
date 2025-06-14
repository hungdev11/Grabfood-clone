package com.app.grabfoodapp.apiservice.reminder;

import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.request.ReminderRequest;
import com.app.grabfoodapp.dto.response.ReminderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReminderService {
    @GET("reminders")
    Call<ApiResponse<List<ReminderResponse>>> getReminders(@Header("Authorization") String token);

    @POST("reminders")
    Call<ApiResponse<ReminderResponse>> createReminder(@Header("Authorization") String token, @Body ReminderRequest request);
    @DELETE("reminders/{id}")
    Call<Void> deleteReminder(@Header("Authorization") String token, @Path("id") Long id);
}
