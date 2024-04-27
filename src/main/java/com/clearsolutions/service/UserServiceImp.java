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

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImp implements UserService {

  private final UserRepository userRepository;
  private final AppConfig appConfig;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto updateUserPartially(UserDto userDto) {
    User user = userRepository.findById(userDto.getId())
        .orElseThrow(() -> new UserNotFoundException(userDto.getId()));
    User updatedUser = userMapper.updateEntityByNotNullValues(userDto, user);
    User savedUser = userRepository.save(updatedUser);
    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional
  public UserDto updateUser(UserDto userDto) {
    User user = userRepository.findById(userDto.getId())
        .orElseThrow(() -> new UserNotFoundException(userDto.getId()));
    User updatedUser = userMapper.mergeWithDto(userDto, user);
    User savedUser = userRepository.save(updatedUser);
    return userMapper.toDto(savedUser);
  }

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
