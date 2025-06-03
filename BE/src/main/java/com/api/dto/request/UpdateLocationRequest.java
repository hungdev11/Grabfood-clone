package com.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLocationRequest {
    
    @NotNull(message = "Vĩ độ không được để trống")
    private Double latitude;
    
    @NotNull(message = "Kinh độ không được để trống")
    private Double longitude;
    
    private Boolean isOnline;
    
    private Long timestamp;
} 