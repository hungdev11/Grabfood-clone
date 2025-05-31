package com.api.repository;

import com.api.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserIdOrderByReminderTimeDesc(Long userId);

    @Query(value = "SELECT * FROM reminders WHERE is_processed = FALSE AND NOW() BETWEEN reminder_time - INTERVAL 15 MINUTE AND reminder_time", nativeQuery = true)
    List<Reminder> findDueReminders();
    void deleteByUserId(Long userId);
}
