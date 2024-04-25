package com.clearsolutions.exceptionhandler.exceptions;

public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE = "User with id=%s not found";

  public UserNotFoundException(long userId) {
    super(MESSAGE.formatted(userId));
  }
}
