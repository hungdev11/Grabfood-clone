package com.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverLoginRequest {
    
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
} 