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
import com.clearsolutions.service.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * The service class for the user entity.
 *
 * @author Oleksandr Semenchenko
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImp implements UserService {

  private final UserRepository userRepository;
  private final AppConfig appConfig;
  private final UserMapper userMapper;

  /**
   * Updates only user's data that are not null in an input object
   * i.e. all null values in the input object are not transmitted to the database.
   *
   * @param userDto - user data
   * @return UserDto
   */
  @Override
  @Transactional
  public UserDto updateUserPartially(UserDto userDto) {
    User user = userRepository.findById(userDto.getId())
        .orElseThrow(() -> new UserNotFoundException(userDto.getId()));

    if (nonNull(userDto.getBirthdate())) {
      verifyUserAge(userDto.getBirthdate());
    }

    if (nonNull(userDto.getEmail())) {
      verifyIfEmailUnique(userDto.getEmail());
    }
    User updatedUser = userMapper.updateEntityByNotNullValues(userDto, user);
    User savedUser = userRepository.save(updatedUser);
    return userMapper.toDto(savedUser);
  }

  /**
   * Updates user data by provided data. The email must be unique
   * and the user's age be greater than the value specified in the configuration file confing.properties.
   *
   * @param userDto - user data
   * @return UserDto
   */
  @Override
  @Transactional
  public UserDto updateUser(UserDto userDto) {
    User user = userRepository.findById(userDto.getId())
        .orElseThrow(() -> new UserNotFoundException(userDto.getId()));
    verifyUserAge(userDto.getBirthdate());
    verifyIfEmailUnique(userDto.getEmail());
    User updatedUser = userMapper.mergeWithDto(userDto, user);
    User savedUser = userRepository.save(updatedUser);
    return userMapper.toDto(savedUser);
  }

  /**
   * Creates user if their age is greater than the value specified in the configuration file confing.properties.
   * The user data must contain email, first name, last name, and birthdate.  Address and phone number are optional.
   * The email must be unique.
   *
   * @param userDto - user data
   * @return UserDto
   */
  @Override
  @Transactional
  public UserDto createUser(UserDto userDto) {
    verifyUserAge(userDto.getBirthdate());
    verifyIfEmailUnique(userDto.getEmail());
    return saveUser(userDto);
  }

  private void verifyUserAge(LocalDate birthdate) {
    Period userAge = Period.between(birthdate, LocalDate.now());
    int minimalYearAge = appConfig.getMinimalAgeInYears();

    if (userAge.getYears() <= minimalYearAge) {
      log.debug("The user's age must be over %s years".formatted(minimalYearAge));
      throw new UserAgeViolationException(minimalYearAge);
    }
  }

  private void verifyIfEmailUnique(String email) {
    if (userRepository.existsByEmail(email)) {
      log.debug("User with email %s already exists".formatted(email));
      throw new EmailNotUniqueException(email);
    }
  }

  private UserDto saveUser(UserDto userDto) {
    User user = userMapper.toEntity(userDto);
    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  /**
   * Searches for users by the provided values for maxBirthdate and minBirthdate of a birthdate range.
   * If the values are not provided returns all users contained in a database.
   *
   * @param searchFilter - searches parameters
   * @param pageable - page settings
   * @return Page<UserDto>
   */
  @Override
  public Page<UserDto> searchUsers(SearchFilter searchFilter, Pageable pageable) {
    verifyPeriod(searchFilter.getMinBirthdate(), searchFilter.getMaxBirthdate());
    Specification<User> specification = UserSpecification.getSpecification(searchFilter);
    return userRepository.findAll(specification, pageable).map(userMapper::toDto);
  }

  private void verifyPeriod(LocalDate from, LocalDate to) {
    if (nonNull(from) && nonNull(to) && from.isAfter(to)) {
      log.debug("The value of maxBirthdate=%s cannot be before minBirthdate=%s".formatted(from, to));
      throw new PeriodNotValidException(from, to);
    }
  }

  @Override
  @Transactional
  public void deleteUserById(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    userRepository.delete(user);
  }
}
