package com.clearsolutions.exceptionhandler.exceptions;

public class EmailNotUniqueException extends RestrictionViolationException {

  private static final String MESSAGE = "User with email %s already exists";

  public EmailNotUniqueException(String email) {
    super(MESSAGE.formatted(email));
  }
}
