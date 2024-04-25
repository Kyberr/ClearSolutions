package com.clearsolutions.service;

import com.clearsolutions.config.AppConfig;
import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.mapper.UserMapper;
import com.clearsolutions.repository.UserRepository;
import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.util.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

  private static final int ONE_YEAR = 1;
  private static final int MINIMAL_AGE_IN_YEAR = 18;
  private static final String USER_MAPPER_FIELD = "userMapper";

  @InjectMocks
  private UserServiceImp userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AppConfig appConfig;

  private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

  @Test
  void createUser_shouldSaveAndReturnUser_whenRequested() {
    ReflectionTestUtils.setField(userService, USER_MAPPER_FIELD, USER_MAPPER);
    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEAR);
    UserDto userDto = TestDataGenerator.generateUserDto();
    LocalDate validUserBirthdate = generateValidUserBirthdate();
    userDto.setBirthdate(validUserBirthdate);
    when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
    User user = TestDataGenerator.generateUserEntity();
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto savedUser = userService.createUser(userDto);

    assertEquals(user.getId(), savedUser.getId());
    assertEquals(user.getEmail(), savedUser.getEmail());
    assertEquals(user.getFirstName(), savedUser.getFirstName());
    assertEquals(user.getLastName(), savedUser.getLastName());
    assertEquals(user.getBirthdate(), savedUser.getBirthdate());
    assertEquals(user.getAddress(), savedUser.getAddress());
    assertEquals(user.getPhoneNumber(), savedUser.getPhoneNumber());
  }

  @Test
  void createUser_shouldThrowEmailNotUniqueException_whenSuchEmailIsAlreadyInDb() {
    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEAR);
    UserDto userDto = TestDataGenerator.generateUserDto();
    LocalDate validUserBirthdate = generateValidUserBirthdate();
    userDto.setBirthdate(validUserBirthdate);
    when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

    assertThrows(EmailNotUniqueException.class, () -> userService.createUser(userDto));
  }

  private LocalDate generateValidUserBirthdate() {
    int validUserAge = appConfig.getMinimalAgeInYears() + ONE_YEAR;
    return LocalDate.now().minusYears(validUserAge);
  }

  @Test
  void createUser_shouldThrowUserAgeViolationException_whenUserAgeIsNotValid() {
    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEAR);

    UserDto userDto = TestDataGenerator.generateUserDto();
    LocalDate notValidUserBirthday = generateNotValidUserBirthday();
    userDto.setBirthdate(notValidUserBirthday);

    assertThrows(UserAgeViolationException.class, () -> userService.createUser(userDto));
  }

  private LocalDate generateNotValidUserBirthday() {
    int notValidUserAge = appConfig.getMinimalAgeInYears() - ONE_YEAR;
    return LocalDate.now().minusYears(notValidUserAge);
  }
}
