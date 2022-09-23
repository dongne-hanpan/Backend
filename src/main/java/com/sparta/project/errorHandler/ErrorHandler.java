package com.sparta.project.errorHandler;

import com.amazonaws.services.kms.model.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> ErrorMessageUserCheck(Exception ex) {
        return ResponseEntity.status(401).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> ErrorMessageServerCheck(Exception ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> ErrorMessageMatchCheck(Exception ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<Object> ErrorMessageImageCheck(Exception ex) {
        return ResponseEntity.status(415).body(ex.getMessage());
    }
}