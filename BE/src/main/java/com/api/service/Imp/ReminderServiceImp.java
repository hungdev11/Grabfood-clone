package com.api.service.Imp;

import com.api.dto.request.ReminderRequest;
import com.api.dto.response.ReminderResponse;
import com.api.entity.Reminder;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ReminderRepository;
import com.api.repository.UserRepository;
import com.api.service.EmailService;
import com.api.service.NotificationService;
import com.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderServiceImp implements ReminderService {
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public ReminderResponse createReminder(Long userId, ReminderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Reminder reminder = Reminder.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .reminderTime(request.getReminderTime())
                .emailEnabled(request.isEmailEnabled())
                .build();

        reminderRepository.save(reminder);
        return ReminderResponse.builder()
                .id(reminder.getId())
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .reminderTime(reminder.getReminderTime())
                .isProcessed(reminder.isProcessed())
                .build();
    }

    @Override
    public List<ReminderResponse> getUserReminders(Long userId) {
        List<Reminder> reminders = reminderRepository.findByUserIdOrderByReminderTimeDesc(userId);
        List<ReminderResponse> response = reminders.stream()
                .map(reminder -> ReminderResponse.builder()
                        .id(reminder.getId())
                        .title(reminder.getTitle())
                        .description(reminder.getDescription())
                        .reminderTime(reminder.getReminderTime())
                        .isProcessed(reminder.isProcessed())
                        .build())
                .toList();
        return response;
    }

    @Override
    @Transactional
    public void deleteReminder(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        reminderRepository.delete(reminder);
    }

    @Override
    @Transactional
    public void deleteAllReminders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        reminderRepository.deleteByUserId(userId);
        log.info("Deleted all reminders for user: {}", userId);
    }

//    @Override
//    @Transactional
//    public Reminder updateReminder(Long userId, Long reminderId, ReminderRequest request) {
//        Reminder reminder = reminderRepository.findById(reminderId)
//                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
//
//        if (!reminder.getUser().getId().equals(userId)) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//
//        reminder.setTitle(request.getTitle());
//        reminder.setDescription(request.getDescription());
//        reminder.setReminderTime(request.getReminderTime());
//        reminder.setEmailEnabled(request.isEmailEnabled());
//
//        return reminderRepository.save(reminder);
//    }

    @Override
    @Scheduled(fixedDelay = 60000) // Run every minute
    @Transactional
    public void processReminders() {
        log.debug("Processing due reminders");
        List<Reminder> dueReminders = reminderRepository.findDueReminders();

        for (Reminder reminder : dueReminders) {
            User user = reminder.getUser();

            try {
                // Send email if enabled
                if (reminder.isEmailEnabled()) {
                    emailService.sendReminderEmail(user, reminder);
                }

                // Mark as processed
                reminder.setProcessed(true);
                reminderRepository.save(reminder);

                log.info("Processed reminder ID: {} for user: {}", reminder.getId(), user.getId());
            } catch (Exception e) {
                log.error("Error processing reminder ID: {}: {}", reminder.getId(), e.getMessage());
            }
        }
    }
}
