package com.clearsolutions.controller;

import com.clearsolutions.exceptionhandler.exceptions.EmailNotUniqueException;
import com.clearsolutions.exceptionhandler.exceptions.PeriodNotValidException;
import com.clearsolutions.exceptionhandler.exceptions.UserAgeViolationException;
import com.clearsolutions.service.UserService;
import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.service.specification.SearchFilter;
import com.clearsolutions.util.TestDataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  private static final String V1 = "/v1";
  private static final String USERS_URL = "/users";
  private static final String MAX_BIRTHDATE = "1970-01-01";
  private static final String MIN_BIRTHDATE = "1965-01-01";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  void search_shouldReturnStatus400_whenSearchParametersAreNotValid() throws Exception {
    when(userService.search(any(SearchFilter.class), any(Pageable.class))).thenThrow(PeriodNotValidException.class);
    mockMvc.perform(get(V1 + USERS_URL).accept(APPLICATION_JSON)
        .param("maxBirthdate", MAX_BIRTHDATE)
        .param("minBirthdate", MIN_BIRTHDATE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").hasJsonPath())
        .andExpect(jsonPath("$.errorCode", is(400)))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

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
