package com.clearsolutions.exceptionhandler.exceptions;

import java.time.LocalDate;

public class PeriodNotValidException extends RestrictionViolationException {

  private static final String MESSAGE = "The value of maxBirthdate=%s cannot be before minBirthdate=%s";

  public PeriodNotValidException(LocalDate maxBirthdate, LocalDate minBirthdate) {
    super(MESSAGE.formatted(maxBirthdate, minBirthdate));
  }
}
