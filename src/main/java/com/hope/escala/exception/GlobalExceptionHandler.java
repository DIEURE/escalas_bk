 
package com.hope.escala.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDeniedException(
            AccessDeniedException ex) {

        ErroResponse erro = new ErroResponse();

        erro.setTimestamp(LocalDateTime.now());

        erro.setStatus(403);

        erro.setError("Forbidden");

        erro.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> handleRuntimeException(
            RuntimeException ex) {

        ErroResponse erro = new ErroResponse();

        erro.setTimestamp(LocalDateTime.now());

        erro.setStatus(400);

        erro.setError("Bad Request");

        erro.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }
}
 
