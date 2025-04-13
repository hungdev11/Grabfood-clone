package com.api.exception;

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

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AppException.class)
    public ErrorResponse handleAppException(AppException exception, WebRequest request) {
        return buildErrorResponse(exception.getMessage(), NOT_FOUND, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException exception, WebRequest request) {
        return buildErrorResponse(exception.getMessage(), BAD_REQUEST, request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception exception, WebRequest request) {
        String errorMessage;

        if (exception instanceof MethodArgumentNotValidException ex && ex.getFieldError() != null) {
            errorMessage = ex.getFieldError().getDefaultMessage();
        } else if (exception instanceof MissingServletRequestParameterException ex) {
            errorMessage = "Required parameter '" + ex.getParameterName() + "' is missing";
        } else {
            errorMessage = "Invalid request";
        }

        return buildErrorResponse(errorMessage, BAD_REQUEST, request);
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
}
