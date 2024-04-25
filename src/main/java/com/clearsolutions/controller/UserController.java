package com.clearsolutions.controller;

import com.clearsolutions.service.UserService;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for managing user data.
 *
 * @author Oleksandr Semenchenko
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private static final String V1 = "/v1";
  private static final String USER_URL = "/users/{id}";

  private final UserService userService;

  /**
   * Creates a user if the data contains a first name, a last name, a birthdate, and an email.
   * The user's age also must be greater than 18 years old and the email must be unique having a valid format.
   *
   * @param user - user data
   * @return ResponseEntity<Void>
   */
  @Operation(
      summary = "Creates a user",
      operationId = "createUser",
      description = "Creates a user that has all required data including valid age and a unique email",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "A user has been crated"
          ),
          @ApiResponse(
              responseCode = "400",
              description = "User data is not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T14:10:54.715989458",
                    "errorCode": 400,
                    "details": "The user's age must be over 18 years old"
                  }
                  """
              ))
          ),
          @ApiResponse(
              responseCode = "409",
              description = "User email is not unique",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:46:10.586265784",
                    "errorCode": 409,
                    "details": "User with email email@com already exists"
                  }
              """
              ))
          )
      }
  )
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> createUser(@RequestBody @Validated  UserDto user) {
    UserDto createdUser = userService.createUser(user);
    URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
        .path(V1 + USER_URL)
        .buildAndExpand(createdUser.getId())
        .toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * Searches for users by birthdate range also contains the validation which checks that “From” is less than “To”.
   *
   * @param searchFilter - search parameters
   * @param pageable - page settings
   * @return Page<UserDto>
   */
  @Operation(
      summary = "Searches for users",
      operationId = "searchUser",
      description = "Searches for users using minBirthdate and maxBirthdate parameters",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Returns a page with users"
          ),
          @ApiResponse(
              responseCode = "400",
              description = "The request parameters are not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:16:45.044736999",
                    "errorCode": 400,
                    "details": "The value of maxBirthdate=2022-03-07 cannot be before minBirthdate=1980-03-07"
                  }
                  """
              ))
          )
      }
  )
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public Page<UserDto> searchUsers(@ParameterObject SearchFilter searchFilter,
                                   @ParameterObject Pageable pageable) {
    return userService.searchUsers(searchFilter, pageable);
  }
}
