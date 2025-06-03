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
     * Xử lý AppException - HYBRID: Driver APIs dùng ApiResponse, Grabfood APIs dùng
     * ErrorResponse
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException exception, WebRequest request) {
        // Nếu là driver API, trả về ApiResponse format mới
        if (isDriverAPI(request)) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(404)
                    .message(exception.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(NOT_FOUND).body(response);
        }

        // 🔥 GRABFOOD APIs: Giữ nguyên logic cũ - trả về ErrorResponse
        ErrorResponse errorResponse = buildErrorResponse(exception.getMessage(), NOT_FOUND, request);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    /**
     * Xử lý RuntimeException - HYBRID
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
        // Nếu là driver API, trả về ApiResponse format mới
        if (isDriverAPI(request)) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + exception.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }

        ErrorResponse errorResponse = buildErrorResponse(exception.getMessage(), BAD_REQUEST, request);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    /**
     * Xử lý validation exceptions
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, MissingServletRequestParameterException.class })
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(Exception exception, WebRequest request) {
        String errorMessage;

        if (exception instanceof MethodArgumentNotValidException ex && ex.getFieldError() != null) {
            errorMessage = ex.getFieldError().getDefaultMessage();
        } else if (exception instanceof MissingServletRequestParameterException ex) {
            // 🔥 GRABFOOD APIs: Giữ nguyên message cũ
            errorMessage = isDriverAPI(request) ? "Thiếu tham số bắt buộc: '" + ex.getParameterName() + "'"
                    : "Required parameter '" + ex.getParameterName() + "' is missing";
        } else {
            errorMessage = isDriverAPI(request) ? "Dữ liệu không hợp lệ" : "Invalid request";
        }

        // Nếu là driver API, trả về ApiResponse format mới
        if (isDriverAPI(request)) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(400)
                    .message(errorMessage)
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        ErrorResponse errorResponse = buildErrorResponse(errorMessage, BAD_REQUEST, request);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Xử lý tất cả exception khác - HYBRID
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception exception, WebRequest request) {
        // Nếu là driver API, trả về ApiResponse format mới
        if (isDriverAPI(request)) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(500)
                    .message("Lỗi hệ thống không xác định")
                    .data(null)
                    .build();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }

        ErrorResponse errorResponse = buildErrorResponse("Lỗi hệ thống không xác định", INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setError(status.getReasonPhrase());
        response.setStatus(status.value());
        response.setMessage(message);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Helper method để phân biệt Driver API vs Grabfood API
     */
    private boolean isDriverAPI(WebRequest request) {
        return request.getDescription(false).contains("/api/driver");
    }
}
