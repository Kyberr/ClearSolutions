package com.clearsolutions.service;

import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

  UserDto createUser(UserDto user);

  Page<UserDto> searchUsers(SearchFilter searchFilter, Pageable pageable);

  void deleteUserById(UUID userId);

  UserDto updateUser(UserDto user);
}
