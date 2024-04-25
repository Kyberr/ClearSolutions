package com.clearsolutions.exceptionhandler.exceptions;

public class UserAgeViolationException extends RestrictionViolationException {

  private static final String MESSAGE = "The user's age must be over %s years old";

  public UserAgeViolationException(int userAge) {
    super(MESSAGE.formatted(userAge));
  }
}
