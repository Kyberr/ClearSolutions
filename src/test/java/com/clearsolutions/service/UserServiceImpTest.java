package com.clearsolutions.service;

import com.clearsolutions.config.AppConfig;
import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.PeriodNotValidException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.mapper.UserMapper;
import com.clearsolutions.repository.UserRepository;
import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import com.clearsolutions.util.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

  private static final int ONE_YEAR = 1;
  private static final int MINIMAL_AGE_IN_YEAR = 18;
  private static final String USER_MAPPER_FIELD = "userMapper";
  private static final LocalDate MAX_BIRTHDATE = LocalDate.of(1970, 1, 1);
  private static final LocalDate MIN_BIRTHDATE = LocalDate.of(1965, 1, 1);

  @InjectMocks
  private UserServiceImp userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AppConfig appConfig;

  private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

  @Test
  void search_shouldThrowPeriodNotValidException_whenMinBirthdateIsAfterMaxBirthdate() {
    SearchFilter searchFilter = SearchFilter.builder()
        .maxBirthdate(MIN_BIRTHDATE)
        .minBirthdate(MAX_BIRTHDATE).build();
    Pageable pageable = Pageable.unpaged();

    assertThrows(PeriodNotValidException.class, () -> userService.search(searchFilter, pageable));
  }

  @Test
  void search_shouldReturnPageWithUsers_whenMinBirthdateAndMaxBirthdateAreNull() {
    ReflectionTestUtils.setField(userService, USER_MAPPER_FIELD, USER_MAPPER);
    SearchFilter searchFilter = new SearchFilter();
    Pageable pageable = Pageable.ofSize(10);
    User user = TestDataGenerator.generateUserEntity();
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    Page<UserDto> foundPage = userService.search(searchFilter, pageable);
    UserDto foundUser = foundPage.getContent().get(0);

    verifyResults(user, foundUser);
  }

  @Test
  void search_shouldReturnPageWithUsers_whenMinBirthdateIsBeforeMaxBirthdate() {
    ReflectionTestUtils.setField(userService, USER_MAPPER_FIELD, USER_MAPPER);
    SearchFilter searchFilter = SearchFilter.builder()
        .minBirthdate(MIN_BIRTHDATE)
        .maxBirthdate(MIN_BIRTHDATE).build();
    Pageable pageable = Pageable.ofSize(10);
    User user = TestDataGenerator.generateUserEntity();
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    Page<UserDto> foundPage = userService.search(searchFilter, pageable);
    UserDto foundUser = foundPage.getContent().get(0);

    verifyResults(user, foundUser);
  }

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

    verifyResults(user, savedUser);
  }

  private void verifyResults(User expectedUserData, UserDto actualUserData) {
    assertEquals(expectedUserData.getId(), actualUserData.getId());
    assertEquals(expectedUserData.getEmail(), actualUserData.getEmail());
    assertEquals(expectedUserData.getFirstName(), actualUserData.getFirstName());
    assertEquals(expectedUserData.getLastName(), actualUserData.getLastName());
    assertEquals(expectedUserData.getBirthdate(), actualUserData.getBirthdate());
    assertEquals(expectedUserData.getAddress(), actualUserData.getAddress());
    assertEquals(expectedUserData.getPhoneNumber(), actualUserData.getPhoneNumber());
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
