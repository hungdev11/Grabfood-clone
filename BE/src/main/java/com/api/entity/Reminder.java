package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reminders")
public class Reminder extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String description;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "is_email_enabled")
    private boolean emailEnabled = true;

    @Column(name = "is_processed")
    private boolean processed = false;
}


