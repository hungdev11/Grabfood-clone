package com.api.dto.request;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectOrderRequest {

    @NotBlank(message = "Reason is required")
    private String reason;
}