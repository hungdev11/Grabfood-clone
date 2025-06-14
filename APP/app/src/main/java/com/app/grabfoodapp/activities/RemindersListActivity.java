package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.ReminderAdapter;
import com.app.grabfoodapp.apiservice.reminder.ReminderService;
import com.app.grabfoodapp.config.ApiClient;
import com.app.grabfoodapp.dto.ApiResponse;
import com.app.grabfoodapp.dto.response.ReminderResponse;
import com.app.grabfoodapp.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemindersListActivity extends AppCompatActivity {
    private ListView listViewReminders;
    private ProgressBar progressBar;
    private TextView emptyText;
    private TokenManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);

        tokenManager = new TokenManager(this);

        // Initialize views
        listViewReminders = findViewById(R.id.list_view_reminders);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton addButton = findViewById(R.id.btn_add_reminder);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup add reminder button
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(RemindersListActivity.this, ReminderActivity.class);
            startActivity(intent);
        });

        // Setup item long click for delete
        listViewReminders.setOnItemLongClickListener((parent, view, position, id) -> {
            ReminderResponse reminder = (ReminderResponse) parent.getItemAtPosition(position);
            showDeleteDialog(reminder);
            return true;
        });

        // Check if user is logged in
        if (!tokenManager.hasToken()) {
            emptyText.setText("Vui lòng đăng nhập để xem nhắc hẹn");
            emptyText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            // Load reminders
            loadReminders();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reload reminders when returning to the activity
        if (tokenManager.hasToken()) {
            loadReminders();
        }
    }
    private void showDeleteDialog(ReminderResponse reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa nhắc hẹn");
        builder.setMessage("Bạn có chắc muốn xóa nhắc hẹn này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteReminder(reminder.getId());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    private void deleteReminder(Long id) {
        String token = "Bearer " + tokenManager.getToken();

        ReminderService reminderService = ApiClient.getClient().create(ReminderService.class);
        reminderService.deleteReminder(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RemindersListActivity.this, "Đã xóa nhắc hẹn", Toast.LENGTH_SHORT).show();
                    loadReminders(); // Reload the list
                } else {
                    Toast.makeText(RemindersListActivity.this, "Không thể xóa nhắc hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RemindersListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadReminders() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        String token = "Bearer " + tokenManager.getToken();

        ReminderService reminderService = ApiClient.getClient().create(ReminderService.class);
        reminderService.getReminders(token).enqueue(new Callback<ApiResponse<List<ReminderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReminderResponse>>> call, Response<ApiResponse<List<ReminderResponse>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<ReminderResponse> reminders = response.body().getData();

                    if (reminders != null && !reminders.isEmpty()) {
                        // Display reminders
                        ReminderAdapter adapter = new ReminderAdapter(RemindersListActivity.this, reminders);
                        listViewReminders.setAdapter(adapter);
                    } else {
                        // Show empty message
                        emptyText.setText("Bạn chưa có nhắc hẹn nào");
                        emptyText.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Show error
                    Toast.makeText(RemindersListActivity.this, "Lỗi khi tải nhắc hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReminderResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RemindersListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}