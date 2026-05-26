package com.myfinwallet.finwallet_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_FUNDS", ex.getMessage());
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleDailyLimit(DailyLimitExceededException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "DAILY_LIMIT_EXCEEDED", ex.getMessage());
    }

    @ExceptionHandler(SuspiciousActivityException.class)
    public ResponseEntity<Map<String, Object>> handleSuspiciousActivity(SuspiciousActivityException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "SUSPICIOUS_ACTIVITY", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Error de Validación");
            return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);           
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Error interno del servidor");
    }
    
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatusCode status, String error, String message) {
        Map<String, Object> body =  new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
    
}
