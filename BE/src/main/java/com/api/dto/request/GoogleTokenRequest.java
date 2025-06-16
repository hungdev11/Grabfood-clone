package com.api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleTokenRequest {
    private String idToken;
}
