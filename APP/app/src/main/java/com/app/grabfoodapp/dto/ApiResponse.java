package com.app.grabfoodapp.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T> {
    private int code;
    private String message;
    private T data;
}
