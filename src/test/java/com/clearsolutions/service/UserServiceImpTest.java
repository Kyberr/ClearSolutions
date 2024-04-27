package com.clearsolutions.service;

import com.clearsolutions.config.AppConfig;
import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.PeriodNotValidException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.exceptionhandler.exceptions.UserNotFoundException;
import com.clearsolutions.mapper.UserMapper;
import com.clearsolutions.repository.UserRepository;
import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import com.clearsolutions.TestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

  private static final int ONE_YEAR = 1;
  private static final int MINIMAL_AGE_IN_YEARS = 18;
  private static final String USER_MAPPER_FIELD = "userMapper";
  private static final LocalDate MAX_BIRTHDATE = LocalDate.of(1970, 1, 1);
  private static final LocalDate MIN_BIRTHDATE = LocalDate.of(1965, 1, 1);
  private static final UUID USER_ID = UUID.randomUUID();

  @InjectMocks
  private UserServiceImp userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AppConfig appConfig;

  @BeforeEach
  void setUp() {
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    ReflectionTestUtils.setField(userService, USER_MAPPER_FIELD, userMapper);
  }

  @Test
  void updateUserPartially_shouldUpdateUser_whenUserIsInDb() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    userDto.setId(USER_ID);
    User user = TestDataGenerator.generateUserEntity();
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    UserDto updatedUser = userService.updateUserPartially(userDto);

    verifyUserDto(user, updatedUser);
  }

  @Test
  void updateUserPartially_shouldThrowEmailNotUniqueException_whenProvidedEmailIsNotUnique() {
    UserDto userDto = buildUserDtoWithValidBirthdate();
    userDto.setId(USER_ID);
    User user = TestDataGenerator.generateUserEntity();
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(any(String.class))).thenReturn(true);

    assertThrows(EmailNotUniqueException.class, () -> userService.updateUserPartially(userDto));
  }

  @Test
  void updateUserPartially_shouldThrowUserAgeViolationException_whenUserAgeIsNotValid() {
    UserDto userDto = buildUserDtoWithNotValidBirthday();
    userDto.setId(USER_ID);
    User user = TestDataGenerator.generateUserEntity();
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

    assertThrows(UserAgeViolationException.class, () -> userService.updateUserPartially(userDto));
  }

  @Test
  void updateUserPartially_shouldThrowUserNotFoundException_whenUserIsNotInDb() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    userDto.setId(USER_ID);
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userService.updateUserPartially(userDto));
  }

  @Test
  void updateUser_shouldThrowNotFoundException_whenUserIsNotInDb() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    userDto.setId(UUID.randomUUID());
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.updateUser(userDto));
  }

  @Test
  void updateUser_shouldUpdateAndReturnUser_whenUserIsInDb() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    User user = TestDataGenerator.generateUserEntity();
    userDto.setId(user.getId());

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto updatedUser = userService.updateUser(userDto);
    verifyUserDto(user, updatedUser);
  }

  @Test
  void deleteUserById_shouldThrowUserNotFoundException_whenUserIsNotInDb() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(USER_ID));
  }

  @Test
  void deleteUserById_shouldDeleteUser_whenUserIsInDb() {
    User user = TestDataGenerator.generateUserEntity();
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    userService.deleteUserById(user.getId());

    verify(userRepository).delete(user);
  }

  @Test
  void searchUsers_shouldThrowPeriodNotValidException_whenBirthdatePeriodIsNotValid() {
    SearchFilter searchFilter = buildFilterWithNotValidBirthdayPeriod();
    Pageable pageable = Pageable.unpaged();

    assertThrows(PeriodNotValidException.class, () -> userService.searchUsers(searchFilter, pageable));
  }

  private SearchFilter buildFilterWithNotValidBirthdayPeriod() {
    return SearchFilter.builder()
        .maxBirthdate(MIN_BIRTHDATE)
        .minBirthdate(MAX_BIRTHDATE).build();
  }

  @Test
  void searchUsers_shouldReturnPageWithAllUsers_whenSearchFilterHasNoBirthdate() {
    SearchFilter emptySearchFilter = new SearchFilter();
    Pageable pageable = Pageable.ofSize(10);
    User user = TestDataGenerator.generateUserEntity();
    Page<User> page = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    Page<UserDto> foundPage = userService.searchUsers(emptySearchFilter, pageable);
    UserDto foundUser = foundPage.getContent().get(0);

    verifyUserDto(user, foundUser);
  }

  @Test
  void searchUsers_shouldReturnPageWithUsers_whenBirthdayPeriodIsValid() {
    SearchFilter filterWithValidBirthdayPeriod = buildFilterWithValidBirthdayPeriod();
    Pageable pageable = Pageable.ofSize(10);
    User user = TestDataGenerator.generateUserEntity();
    Page<User> page = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    Page<UserDto> recievedPage = userService.searchUsers(filterWithValidBirthdayPeriod, pageable);
    UserDto receivedUser = recievedPage.getContent().get(0);

    verifyUserDto(user, receivedUser);
  }

  private SearchFilter buildFilterWithValidBirthdayPeriod() {
    return SearchFilter.builder()
        .minBirthdate(MIN_BIRTHDATE)
        .maxBirthdate(MIN_BIRTHDATE).build();
  }

  @Test
  void createUser_shouldSaveAndReturnUser_whenUserDataIsValid() {
    UserDto userDto = buildUserDtoWithValidBirthdate();
    User user = TestDataGenerator.generateUserEntity();

    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEARS);
    when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto savedUser = userService.createUser(userDto);

    verifyUserDto(user, savedUser);
  }

  private void verifyUserDto(User expectedUser, UserDto actualUser) {
    assertEquals(expectedUser.getId(), actualUser.getId());
    assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
    assertEquals(expectedUser.getLastName(), actualUser.getLastName());
    assertEquals(expectedUser.getBirthdate(), actualUser.getBirthdate());
    assertEquals(expectedUser.getAddress(), actualUser.getAddress());
    assertEquals(expectedUser.getPhoneNumber(), actualUser.getPhoneNumber());
  }

  @Test
  void createUser_shouldThrowEmailNotUniqueException_whenSuchEmailIsAlreadyInDb() {
    UserDto userWithNotUniqueEmail = buildUserDtoWithValidBirthdate();

    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEARS);
    when(userRepository.existsByEmail(userWithNotUniqueEmail.getEmail())).thenReturn(true);

    assertThrows(EmailNotUniqueException.class, () -> userService.createUser(userWithNotUniqueEmail));
  }

  private UserDto buildUserDtoWithValidBirthdate() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    LocalDate validUserBirthdate = generateValidUserBirthdate();
    userDto.setBirthdate(validUserBirthdate);
    return userDto;
  }

  private LocalDate generateValidUserBirthdate() {
    int validUserAge = MINIMAL_AGE_IN_YEARS + ONE_YEAR;
    return LocalDate.now().minusYears(validUserAge);
  }

  @Test
  void createUser_shouldThrowUserAgeViolationException_whenUserAgeIsNotValid() {
    UserDto userWithNotValidBirthday = buildUserDtoWithNotValidBirthday();
    when(appConfig.getMinimalAgeInYears()).thenReturn(MINIMAL_AGE_IN_YEARS);

    assertThrows(UserAgeViolationException.class, () -> userService.createUser(userWithNotValidBirthday));
  }

  private UserDto buildUserDtoWithNotValidBirthday() {
    UserDto userDto = TestDataGenerator.generateUserDto();
    LocalDate notValidUserBirthday = generateNotValidUserBirthday();
    userDto.setBirthdate(notValidUserBirthday);
    return userDto;
  }

  private LocalDate generateNotValidUserBirthday() {
    int notValidUserAge = appConfig.getMinimalAgeInYears() - ONE_YEAR;
    return LocalDate.now().minusYears(notValidUserAge);
  }
}
