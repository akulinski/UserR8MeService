package com.akulinski.keepmeawake.web.rest;

import com.akulinski.keepmeawake.core.domain.dto.ExceptionDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO("Internal Application Error ", new Date().toInstant());
        return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}
