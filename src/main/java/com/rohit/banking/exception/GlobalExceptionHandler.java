package com.rohit.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    // 🟡 Validation error (Controller level)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                error.put(err.getField(), err.getDefaultMessage())
        );

        return error;
    }

    // 🟠 Validation error (Hibernate / DB level)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();

        ex.getConstraintViolations().forEach(err ->
                error.put(err.getPropertyPath().toString(), err.getMessage())
        );

        return error;
    }

    // ⚫ Generic fallback (ALWAYS LAST)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleGeneric(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}