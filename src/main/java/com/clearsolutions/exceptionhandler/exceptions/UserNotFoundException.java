package com.clearsolutions.exceptionhandler.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE = "User with id=%s not found";

  public UserNotFoundException(UUID userId) {
    super(MESSAGE.formatted(userId));
  }
}
