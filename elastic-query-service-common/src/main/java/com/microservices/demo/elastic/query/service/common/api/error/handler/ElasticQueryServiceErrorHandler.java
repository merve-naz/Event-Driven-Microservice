package com.microservices.demo.elastic.query.service.common.api.error.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice //tüm controller'lar için ortak davranış tanımlamaya yarar.
public class ElasticQueryServiceErrorHandler {
    // 1. en büyük ve en popüler görevi hata yakalamaktır (@ExceptionHandler).
    // 2. tüm endpoint'lere ortak veri üflemek (@ModelAttribute) ve gelen verileri metoda girmeden biçimlendirmek (@InitBinder) i

    private static final Logger LOG = LoggerFactory.getLogger(ElasticQueryServiceErrorHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handle(AccessDeniedException ex) {
        return ResponseEntity.status(403).body("Access Denied: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException e) {
        LOG.error("Illegal argument exception!", e);
        return ResponseEntity.badRequest().body("Illegal argument exception! " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e) {
        LOG.error("Service runtime exception!", e);
        return ResponseEntity.badRequest().body("Service runtime exception! " + e.getMessage());
    }// NullPointerException ve IndexOutOfBoundsException

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        LOG.error("Internal server error!", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A server error occurred!");
    }

    //@ExceptionHandler'larda yazılış sırası değil, inheritance ağacındaki en yakın tip önemlidir.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e) {
        LOG.error("Method argument validation exception!", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }




}
