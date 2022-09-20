package com.sparta.project.errorHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Object> ErrorMessage(Exception ex) {
        return ResponseEntity.ok().body(ex.getMessage());
    }

}
