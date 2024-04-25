package com.clearsolutions.service;

import com.clearsolutions.config.AppConfig;
import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.mapper.UserMapper;
import com.clearsolutions.repository.UserRepository;
import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImp implements UserService {

  private final UserRepository userRepository;
  private final AppConfig appConfig;
  private final UserMapper userMapper;

  @Override
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
}
