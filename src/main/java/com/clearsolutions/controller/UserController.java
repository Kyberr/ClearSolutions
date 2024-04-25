package com.clearsolutions.controller;

import com.clearsolutions.service.UserService;
import com.clearsolutions.service.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private static final String V1 = "/v1";
  private static final String USER_URL = "/users/{id}";

  private final UserService userService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> createUser(@RequestBody @Validated  UserDto user) {
    UserDto createdUser = userService.createUser(user);
    URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
        .path(V1 + USER_URL)
        .buildAndExpand(createdUser.getId())
        .toUri();
    return ResponseEntity.created(location).build();
  }
}
