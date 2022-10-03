package com.sparta.project.errorHandler;

import com.amazonaws.services.kms.model.InvalidGrantTokenException;
import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.ExceptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> ErrorMessageUserCheck(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(401)
                .build();

        return ResponseEntity.status(401).body(exceptionResponseDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> ErrorMessageServerCheck(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(500)
                .build();

        return ResponseEntity.status(500).body(exceptionResponseDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> ErrorMessageMatchCheck(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(404)
                .build();

        return ResponseEntity.status(404).body(exceptionResponseDto);
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<Object> ErrorMessageImageCheck(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(415)
                .build();

        return ResponseEntity.status(415).body(exceptionResponseDto);
    }

    @ExceptionHandler(InvalidGrantTokenException.class)
    public ResponseEntity<Object> ErrorMessageToken(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(403)
                .build();

        return ResponseEntity.status(403).body(exceptionResponseDto);
    }
}