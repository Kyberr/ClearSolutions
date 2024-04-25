package com.clearsolutions.exceptionhandler;

import com.clearsolutions.exceptionhandler.exceptions.RestrictionViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ServiceExceptionHandler {

  private static final String DETAILS_FIELD = "details";
  private static final String ERROR_CODE_FIELD = "errorCode";
  private static final String TIMESTAMP_FILED = "timestamp";

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

  //TODO
}
