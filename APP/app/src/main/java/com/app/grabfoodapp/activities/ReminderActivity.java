package com.app.grabfoodapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.apiservice.reminder.ReminderService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.request.ReminderRequest;
import com.app.grabfoodapp.dto.response.ReminderResponse;
import com.app.grabfoodapp.utils.TokenManager;


import org.threeten.bp.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editDescription;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_reminder);
        tokenManager = new TokenManager(this);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        Button btnSaveReminder = findViewById(R.id.btn_save_reminder);
        ImageButton btnBack = findViewById(R.id.reminder_back_btn);

        btnBack.setOnClickListener(v -> finish());
        btnSaveReminder.setOnClickListener(v -> {
            // Save reminder
            saveReminder();

        });
    }

    private void saveReminder() {
        // Check if user is logged in
        if (!tokenManager.hasToken()) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get date
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        // Get time
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Create LocalDateTime object
        LocalDateTime reminderDateTime = LocalDateTime.of(year, month + 1, day, hour, minute);

        // Validate future date
        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            Toast.makeText(this, "Vui lòng chọn thời gian trong tương lai", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request
        ReminderRequest request = ReminderRequest.builder()
                .title(title)
                .description(description)
                .reminderTime(reminderDateTime)
                .emailEnabled(true) // Default to true
                .build();

        // Send request to API
        saveReminderToServer(request);
    }
    private void saveReminderToServer(ReminderRequest request) {
        String token = "Bearer " + tokenManager.getToken();

        ReminderService reminderService = ApiClient.getClient().create(ReminderService.class);
        reminderService.createReminder(token, request).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                if (response.isSuccessful()) {
                    // Get date and time for toast message
                    LocalDateTime time = request.getReminderTime();
                    String minuteStr = time.getMinute() < 10 ? "0" + time.getMinute() : String.valueOf(time.getMinute());
                    String hourStr = time.getHour() < 10 ? "0" + time.getHour() : String.valueOf(time.getHour());

                    Toast.makeText(ReminderActivity.this, "Đã đặt nhắc hẹn vào " +
                            time.getDayOfMonth() + "/" + time.getMonthValue() + "/" + time.getYear() +
                            " lúc " + hourStr + ":" + minuteStr, Toast.LENGTH_SHORT).show();

                    // Close activity
                    finish();
                } else {
                    // Handle error
                    Toast.makeText(ReminderActivity.this, "Lỗi: Không thể lưu nhắc hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                Toast.makeText(ReminderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}