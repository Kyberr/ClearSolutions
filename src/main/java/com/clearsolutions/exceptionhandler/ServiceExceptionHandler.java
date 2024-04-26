package com.clearsolutions.exceptionhandler;

import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.NotFoundException;
import com.clearsolutions.exceptionhandler.exceptions.RestrictionViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ServiceExceptionHandler {

  private static final String DETAILS_FIELD = "details";
  private static final String ERROR_CODE_FIELD = "errorCode";
  private static final String TIMESTAMP_FILED = "timestamp";

  @ExceptionHandler(TypeMismatchException.class)
  protected ResponseEntity<Object> handleTypeMismatchException(TypeMismatchException e) {
    Map<String, Object> responseBody = buildErrorResponseBody(HttpStatus.BAD_REQUEST, e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
  }

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
    Map<String, Object> responseBody = buildErrorResponseBody(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
  }

  @ExceptionHandler(EmailNotUniqueException.class)
  protected ResponseEntity<Object> handleEmailNotUniqueException(EmailNotUniqueException e) {
    Map<String, Object> responseBody = buildErrorResponseBody(HttpStatus.CONFLICT, e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    Map<String, String> validationDetails = getMethodArgumentValidationDetails(e);
    Map<String, Object> responseBody = buildErrorResponseBody(HttpStatus.BAD_REQUEST, validationDetails);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(responseBody);
  }

  private Map<String, String> getMethodArgumentValidationDetails(MethodArgumentNotValidException e) {
    return e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            DefaultMessageSourceResolvable::getDefaultMessage));
  }

  @ExceptionHandler(RestrictionViolationException.class)
  protected ResponseEntity<Object> handleRestrictionViolationException(RestrictionViolationException e) {
    Map<String, Object> responseBody = buildErrorResponseBody(HttpStatus.BAD_REQUEST, e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(responseBody);
  }

  private Map<String, Object> buildErrorResponseBody(HttpStatus status, Object message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put(TIMESTAMP_FILED, LocalDateTime.now());
    body.put(ERROR_CODE_FIELD, status.value());
    body.put(DETAILS_FIELD, message);
    return body;
  }
}
