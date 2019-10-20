package com.akulinski.userr8meservice.config;

import com.akulinski.userr8meservice.core.domain.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ExceptionDTO exceptionDTO = new ExceptionDTO(e.getMessage(), new Date().toInstant());
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    }
}
