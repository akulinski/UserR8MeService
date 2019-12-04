package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.dto.ExceptionDTO;
import com.akulinski.userr8meservice.core.exceptions.NoCommentException;
import com.akulinski.userr8meservice.core.exceptions.repository.UserNotFoundException;
import com.akulinski.userr8meservice.core.exceptions.validation.FollowException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidEmailException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidPasswordException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidUsernameException;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity handleConflict(RuntimeException ex, WebRequest request) {
    log.error(ex.getMessage());

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {UserNotFoundException.class})
  public ResponseEntity handleUserNotFound(UserNotFoundException ex, WebRequest request){
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    log.error(ex.getLocalizedMessage());

    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {NoCommentException.class})
  public ResponseEntity handleNoCommentException(NoCommentException ex, WebRequest request){
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    log.error(ex.getLocalizedMessage());

    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {MongoWriteException.class})
  public ResponseEntity handleDuplicateKey(MongoWriteException ex, WebRequest request) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = {InvalidEmailException.class})
  public ResponseEntity handleInvalidEmail(InvalidEmailException invalidEmailException, WebRequest webRequest) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(invalidEmailException.getMessage(), new Date().toInstant());
    return handleExceptionInternal(invalidEmailException, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  @ExceptionHandler(value = {InvalidPasswordException.class})
  public ResponseEntity handleInvalidPassword(InvalidPasswordException ex, WebRequest webRequest) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  @ExceptionHandler(value = {InvalidUsernameException.class})
  public ResponseEntity handleInvalidUsername(InvalidUsernameException ex, WebRequest webRequest) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }

  @ExceptionHandler(value = {FollowException.class})
  public ResponseEntity handleFollowException(FollowException ex, WebRequest webRequest) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), new Date().toInstant());
    return handleExceptionInternal(ex, exceptionDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
  }
}
