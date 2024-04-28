package com.clearsolutions.controller;

import com.clearsolutions.config.AppConfig;
import com.clearsolutions.service.UserService;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller which supports end points to manage users.
 *
 * @author Oleksandr Semenchenko
 */
@OpenAPIDefinition(info = @Info(
    title = "Users service API",
    version = "${build.version}",
    description = "The API to manage users"),
    servers = @Server(url = "http://localhost:${server.port}", description = "Development server"))
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private static final String V1 = "/v1";
  private static final String USER_URL = "/users/{id}";

  private final UserService userService;
  private final AppConfig appConfig;

  /**
   * Creates a user if the data contains a first name, a last name, a birthdate and an email.
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
              description = "A user has been crated"),
          @ApiResponse(
              responseCode = "400",
              description = "User data is not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T14:10:54.715989458",
                    "errorCode": 400,
                    "details": "The user's age must be over 18 years old"
                  }
                  """))),
          @ApiResponse(
              responseCode = "409",
              description = "User email is not unique",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:46:10.586265784",
                    "errorCode": 409,
                    "details": "User with email email@com already exists"
                  }
              """)))
      })
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> createUser(@RequestBody @Validated UserDto user) {
    UserDto createdUser = userService.createUser(user);
    URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
        .path(V1 + USER_URL)
        .buildAndExpand(createdUser.getId())
        .toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * Searches for users by birthdate range. Before the searching it checks that “From” is less than “To”.
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
              description = "Returns a page with users"),
          @ApiResponse(
              responseCode = "400",
              description = "The request parameters are not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:16:45.044736999",
                    "errorCode": 400,
                    "details": "The value of maxBirthdate=2022-03-07 cannot be before minBirthdate=1980-03-07"
                  }
                  """)))
      })
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public Page<UserDto> searchUsers(@ParameterObject SearchFilter searchFilter,
                                   @ParameterObject Pageable pageable) {
    return userService.searchUsers(searchFilter, pageable);
  }

  /**
   * Deletes a user if their present in the database.
   *
   * @param userId - a user ID
   */
  @Operation(
      summary = "Deletes a user",
      operationId = "deleteUserById",
      description = "Deletes a user if their present in the database",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "A user was deleted successfully"),
          @ApiResponse(
              responseCode = "400",
              description = "Wrong user ID format",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-28T02:25:24.693145097",
                    "errorCode": 400,
                    "details": "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; \
                    Invalid UUID string: 776c0aed-72fa-45d8-a"
                  }
                  """))),
          @ApiResponse(
              responseCode = "404",
              description = "The user was not found in a database",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-26T09:22:53.840331928",
                    "errorCode": 404,
                    "details": "User with id=776c0aed-72fa-45d8-a65a-8f3ae131097f not found"
                  }
                  """)))
      })
  @ResponseStatus(NO_CONTENT)
  @DeleteMapping(value = "/{userId}")
  public void deleteUser(
      @Parameter(description = "a user ID", example = "4d57987f-600b-4b88-8294-70b9cefb0a98")
      @PathVariable UUID userId) {
    userService.deleteUserById(userId);
  }

  /**
   * Updates user data with the provided data.
   *
   * @param userId - a user ID
   * @param user - user data
   */
  @Operation(
      summary = "Updates a user",
      operationId = "updateUser",
      description = "Updates a user if their present in the database",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Updates user with the provided data"),
          @ApiResponse(
              responseCode = "400",
              description = "User data is not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T14:10:54.715989458",
                    "errorCode": 400,
                    "details": "The user's age must be over 18 years old"
                  }
                  """))),
          @ApiResponse(
              responseCode = "404",
              description = "A user was not found in the database",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-26T09:22:53.840331928",
                    "errorCode": 404,
                    "details": "User with id=776c0aed-72fa-45d8-a65a-8f3ae131097f not found"
                  }
                  """))),
          @ApiResponse(
              responseCode = "409",
              description = "User email is not unique",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:46:10.586265784",
                    "errorCode": 409,
                    "details": "User with email email@com already exists"
                  }
              """)))
      })
  @ResponseStatus(OK)
  @PutMapping(value = "/{userId}")
  public void updateUser(
      @Parameter(description = "a user ID", example = "4d57987f-600b-4b88-8294-70b9cefb0a98")
      @PathVariable UUID userId,
      @RequestBody @Validated UserDto user) {
    user.setId(userId);
    userService.updateUser(user);
  }

  /**
   * Updates only user's fields that are not null in an input object.
   *
   * @param userId - a user ID
   * @param user - user data
   */
  @Operation(
      summary = "Updates partially a user",
      operationId = "updateUserPartially",
      description = "Updates only user's fields that are not null in the input object",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Updates partially a user"),
          @ApiResponse(
              responseCode = "400",
              description = "User data is not valid",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T14:10:54.715989458",
                    "errorCode": 400,
                    "details": "The user's age must be over 18 years old"
                  }
                  """))),
          @ApiResponse(
              responseCode = "404",
              description = "A user not found",
              content = @Content(examples = @ExampleObject("""
                   {
                    "timestamp": "2024-04-26T09:22:53.840331928",
                    "errorCode": 404,
                    "details": "User with id=776c0aed-72fa-45d8-a65a-8f3ae131097f not found"
                  }
                  """))),
          @ApiResponse(
              responseCode = "409",
              description = "User email is not unique",
              content = @Content(examples = @ExampleObject("""
                  {
                    "timestamp": "2024-04-25T21:46:10.586265784",
                    "errorCode": 409,
                    "details": "User with email email@com already exists"
                  }
              """)))
      })
  @ResponseStatus(OK)
  @PatchMapping(value = "/{userId}")
  public void updateUserPartially(
      @Parameter(description = "a user ID", example = "4d57987f-600b-4b88-8294-70b9cefb0a98")
      @PathVariable UUID userId,
      @RequestBody UserDto user) {
    user.setId(userId);
    userService.updateUserPartially(user);
  }
}
