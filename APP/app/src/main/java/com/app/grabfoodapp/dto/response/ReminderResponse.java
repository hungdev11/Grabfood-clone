package com.app.grabfoodapp.dto.response;



import org.threeten.bp.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
