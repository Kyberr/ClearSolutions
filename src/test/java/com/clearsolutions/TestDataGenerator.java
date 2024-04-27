package com.clearsolutions;

import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;

import java.time.LocalDate;
import java.util.UUID;

public class TestDataGenerator {

  private static final String USER_EMAIL = "user@email";
  private static final String FIRST_NAME = "Dennis";
  private static final String LAST_NAME = "Ritchie";
  private static final LocalDate BIRTHDATE = LocalDate.of(1941, 9, 9);
  private static final String USER_ADDRESS = "some address";
  private static final String USER_PHONE_NUMBER = "+38(097)-000-00-00";
  private static final UUID USER_ID = UUID.randomUUID();

  public static UserDto generateUserDto() {
    return  UserDto.builder().firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(USER_EMAIL)
        .birthdate(BIRTHDATE)
        .address(USER_ADDRESS)
        .phoneNumber(USER_PHONE_NUMBER).build();
  }

  public static User generateUserEntity() {
    return User.builder().id(USER_ID)
        .email(USER_EMAIL)
        .birthdate(BIRTHDATE)
        .address(USER_ADDRESS)
        .phoneNumber(USER_PHONE_NUMBER).build();
  }
}
