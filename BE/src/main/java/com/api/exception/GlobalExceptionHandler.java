package com.api.exception;

import com.api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException exception, WebRequest request) {
        return isDriverAPI(request)
                ? ResponseEntity.status(NOT_FOUND)
                        .body(shipperExceptionError(exception.getMessage(), NOT_FOUND.value()))
                : ResponseEntity.status(OK).body(buildErrorResponse(exception.getMessage(), BAD_REQUEST, request));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException exception, WebRequest request) {
        return isDriverAPI(request)
                ? ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                        shipperExceptionError("Lỗi hệ thống: " + exception.getMessage(), INTERNAL_SERVER_ERROR.value()))
                : ResponseEntity.status(OK)
                        .body(buildErrorResponse(exception.getMessage(), INTERNAL_SERVER_ERROR, request));
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, MissingServletRequestParameterException.class })
    public ResponseEntity<?> handleValidationExceptions(Exception exception, WebRequest request) {
        String errorMessage;

        if (exception instanceof MethodArgumentNotValidException ex && ex.getFieldError() != null) {
            errorMessage = ex.getFieldError().getDefaultMessage();
        } else if (exception instanceof MissingServletRequestParameterException ex) {
            errorMessage = isDriverAPI(request)
                    ? "Thiếu tham số bắt buộc: '" + ex.getParameterName() + "'"
                    : "Required parameter '" + ex.getParameterName() + "' is missing";
        } else {
            errorMessage = isDriverAPI(request) ? "Dữ liệu không hợp lệ" : "Invalid request";
        }

        return isDriverAPI(request)
                ? ResponseEntity.status(BAD_REQUEST).body(shipperExceptionError(errorMessage, BAD_REQUEST.value()))
                : ResponseEntity.status(BAD_REQUEST).body(buildErrorResponse(errorMessage, BAD_REQUEST, request));
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

    private boolean isDriverAPI(WebRequest request) {
        return request.getDescription(false).contains("/api/driver");
    }

    private ApiResponse<?> shipperExceptionError(String message, int code) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}