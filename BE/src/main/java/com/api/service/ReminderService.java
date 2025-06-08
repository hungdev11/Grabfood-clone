package com.api.service;

import com.api.dto.request.ReminderRequest;
import com.api.dto.response.ReminderResponse;
import com.api.entity.Reminder;

import java.util.List;

public interface ReminderService {
    ReminderResponse createReminder(Long userId, ReminderRequest request);
    List<ReminderResponse> getUserReminders(Long userId);
    void processReminders();
    void deleteReminder(Long reminderId);
    void deleteAllReminders(Long userId);
    //Reminder updateReminder(Long userId, Long reminderId, ReminderRequest request);

}
