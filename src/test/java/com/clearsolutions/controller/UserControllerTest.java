package com.clearsolutions.controller;

import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.service.UserService;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.util.TestDataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  private static final String V1 = "/v1";
  private static final String USERS_URL = "/users";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  void createUser_shouldReturnStatus400_whenUserHasNotValidAge() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.createUser(any(UserDto.class))).thenThrow(UserAgeViolationException.class);

    mockMvc.perform(post(V1 + USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").hasJsonPath())
        .andExpect(jsonPath("$.errorCode", is(400)))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  void createUser_shouldReturnStatus400_whenEmailIsNotUnique() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.createUser(any(UserDto.class))).thenThrow(EmailNotUniqueException.class);

    mockMvc.perform(post(V1 + USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").hasJsonPath())
        .andExpect(jsonPath("$.errorCode", is(400)))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }



  @Test
  void createUser_shouldReturnStatus400_whenRequestBodyHasBadFormat() throws Exception {
    String notValidEmail = "addfd";
    UserDto userDto = UserDto.builder().email(notValidEmail).build();
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

    mockMvc.perform(post(V1 + USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details.email").isNotEmpty())
        .andExpect(jsonPath("$.details.lastName").isNotEmpty())
        .andExpect(jsonPath("$.details.firstName").isNotEmpty())
        .andExpect(jsonPath("$.details.birthdate").isNotEmpty())
        .andExpect(jsonPath("$.errorCode", is(400)))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }
}
