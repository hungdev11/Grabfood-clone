package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.grabfoodapp.R;
import com.app.grabfoodapp.dto.response.ReminderResponse;

import org.threeten.bp.LocalDateTime;

import java.util.List;

public class ReminderAdapter extends ArrayAdapter<ReminderResponse> {

    private final Context context;
    private final List<ReminderResponse> reminders;

    public ReminderAdapter(@NonNull Context context, @NonNull List<ReminderResponse> reminders) {
        super(context, R.layout.item_reminder, reminders);
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_reminder, parent, false);
        }

        ReminderResponse reminder = reminders.get(position);

        TextView textTitle = view.findViewById(R.id.text_title);
        TextView textDate = view.findViewById(R.id.text_date);
        TextView textTime = view.findViewById(R.id.text_time);
        TextView textDescription = view.findViewById(R.id.text_description);

        // Handle null values to prevent NPE
        textTitle.setText(reminder.getTitle() != null ? reminder.getTitle() : "");
        textDescription.setText(reminder.getDescription() != null ? reminder.getDescription() : "");

        LocalDateTime dateTime = reminder.getReminderTime();
        if (dateTime != null) {
            String formattedDate = String.format("%02d/%02d/%d",
                    dateTime.getDayOfMonth(),
                    dateTime.getMonthValue(),
                    dateTime.getYear());

            String formattedTime = String.format("%02d:%02d",
                    dateTime.getHour(),
                    dateTime.getMinute());

            textDate.setText(formattedDate);
            textTime.setText(formattedTime);
        } else {
            textDate.setText("--/--/----");
            textTime.setText("--:--");
        }

        return view;
    }
}