package com.api.exception;

import com.api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý AppException - trả về JSON response đúng format
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException exception, WebRequest request) {
        // Nếu là driver API, trả về format đặc biệt
        if (request.getDescription(false).contains("/api/driver")) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(400)
                    .message(exception.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        
        // Các API khác vẫn dùng ErrorResponse như cũ
        return ResponseEntity.status(NOT_FOUND).body(ApiResponse.builder()
                .code(404)
                .message(exception.getMessage())
                .data(null)
                .build());
    }

    /**
     * Xử lý RuntimeException - trả về JSON response đúng format
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException exception, WebRequest request) {
        // Nếu là driver API, trả về format đặc biệt
        if (request.getDescription(false).contains("/api/driver")) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + exception.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }
        
        // Các API khác
        return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.builder()
                .code(400)
                .message(exception.getMessage())
                .data(null)
                .build());
    }

    /**
     * Xử lý validation exceptions - trả về JSON response đúng format
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(Exception exception, WebRequest request) {
        String errorMessage;

        if (exception instanceof MethodArgumentNotValidException ex && ex.getFieldError() != null) {
            errorMessage = ex.getFieldError().getDefaultMessage();
        } else if (exception instanceof MissingServletRequestParameterException ex) {
            errorMessage = "Thiếu tham số bắt buộc: '" + ex.getParameterName() + "'";
        } else {
            errorMessage = "Dữ liệu không hợp lệ";
        }

        // Nếu là driver API, trả về format đặc biệt
        if (request.getDescription(false).contains("/api/driver")) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(400)
                    .message(errorMessage)
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        
        // Các API khác vẫn dùng ErrorResponse như cũ
        return ResponseEntity.badRequest().body(buildErrorResponse(errorMessage, BAD_REQUEST, request));
    }

    /**
     * Xử lý tất cả exception khác
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception exception, WebRequest request) {
        // Nếu là driver API, trả về format đặc biệt
        if (request.getDescription(false).contains("/api/driver")) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(500)
                    .message("Lỗi hệ thống không xác định")
                    .data(null)
                    .build();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }
        
        // Các API khác
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(buildErrorResponse(
                "Lỗi hệ thống không xác định", INTERNAL_SERVER_ERROR, request));
    }

    /**
     * Build ErrorResponse cho các API không phải driver (để backward compatibility)
     */
    private ApiResponse<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        return ApiResponse.builder()
                .code(status.value())
                .message(message)
                .data(null)
                .build();
    }
}
