package com.microservices.demo.elastic.query.web.client.common.api.error.handler;


import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ElasticQueryWebClientErrorHandler {

    // Log yazmak için kullanılır.
    private static final Logger LOG =
            LoggerFactory.getLogger(ElasticQueryWebClientErrorHandler.class);

    // Yetkisiz erişim (403) oluşursa çalışır.
    @ExceptionHandler(AccessDeniedException.class)
    public String handle(AccessDeniedException e, Model model) {

        LOG.error("Access denied exception!");

        // Thymeleaf'e hata bilgileri gönderilir.
        model.addAttribute("error",
                HttpStatus.UNAUTHORIZED.getReasonPhrase());

        model.addAttribute("error_description",
                "You are not authorized to access this resource!");

        // error.html sayfası açılır.
        return "error";
    }

    // IllegalArgumentException oluşursa çalışır.
    @ExceptionHandler(IllegalArgumentException.class)
    public String handle(IllegalArgumentException e, Model model) {

        LOG.error("Illegal argument exception!", e);

        model.addAttribute("error",
                HttpStatus.BAD_REQUEST.getReasonPhrase());

        model.addAttribute(
                "error_description",
                "Illegal argument exception! " + e.getMessage());

        return "error";
    }

    // Yakalanmayan tüm Exception'lar için çalışır.
    @ExceptionHandler(Exception.class)
    public String handle(Exception e, Model model) {

        LOG.error("Internal server error!", e);

        model.addAttribute("error",
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        model.addAttribute(
                "error_description",
                "A server error occurred!");

        return "error";
    }

    // RuntimeException oluşursa çalışır.
    @ExceptionHandler(RuntimeException.class)
    public String handle(RuntimeException e, Model model) {

        LOG.error("Service runtime exception!", e);

        // Home sayfasındaki form tekrar oluşturulur.
        model.addAttribute(
                "elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        model.addAttribute(
                "error",
                "Could not get response! " + e.getMessage());

        model.addAttribute(
                "error_description",
                "Service runtime exception! " + e.getMessage());

        // Tekrar home.html açılır.
        return "home";
    }

    // Validation (BindException) oluşursa çalışır.
    @ExceptionHandler(BindException.class)
    public String handle(BindException e, Model model) {

        LOG.error("Method argument validation exception!", e);

        // Alan bazlı validation hatalarını tutacak Map
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult()
                .getAllErrors()
                .forEach(error ->
                        errors.put(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()));

        // Form tekrar oluşturulur.
        model.addAttribute(
                "elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        model.addAttribute(
                "error",
                HttpStatus.BAD_REQUEST.getReasonPhrase());

        // Validation hataları Thymeleaf'e gönderilir.
        model.addAttribute(
                "error_description",
                errors);

        return "home";
    }
}
//BindException, Spring'in formdan gelen veriyi Java nesnesine bağlarken (binding yaparken)
//hata oluştuğunda fırlattığı exception'lardan biridir.