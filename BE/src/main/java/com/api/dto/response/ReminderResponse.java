package com.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private boolean isProcessed;
}
