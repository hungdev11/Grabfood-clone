package com.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderRequest {
    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private boolean emailEnabled = true;
}
