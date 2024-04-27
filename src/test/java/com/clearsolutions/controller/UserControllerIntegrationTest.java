package com.clearsolutions.controller;

import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.TestDataGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/db/users-data.sql")
@Transactional
public class UserControllerIntegrationTest {

  private static final String V1 = "/v1";
  private static final String USERS_URL = "/users";
  private static final String USER_URL = "/users/{userId}";
  private static final String LOCATION_HEADER_FIELD = "Location";
  private static final String EMAIL = "email@com";
  private static final String FIRST_NAME = "Linus";
  private static final String LAST_NAME = "Torvalds";
  private static final String BIRTHDATE = "1969-12-28";
  private static final String ADDRESS = "some address";
  private static final String PHONE_NUMBER = "+38(097)-100-00-00";
  private static final String USER_ID = "92f226ce-f1a0-4514-9466-e811648a5218";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void updateUserPartially_shouldReturnStatus200_whenUserIsInDb() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);

    mockMvc.perform(patch(V1 + USER_URL, USER_ID).contentType(APPLICATION_JSON)
          .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void updateUser_shouldReturnStatus200_whenUserIsInDb() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);

    mockMvc.perform(put(V1 + USER_URL, USER_ID).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void deleteUser_shouldReturnStatus204AndDeleteUser_whenUserIsInDb() throws Exception {
    String userIdInDb = "33e1b468-f030-431e-b48c-09e6d584b51c";
    mockMvc.perform(delete(V1 + USER_URL, userIdInDb))
        .andExpect(status().isNoContent());
  }

  @Test
  void searchUsers_shouldReturnStatus200AndBody_whenBirthdayPeriodIsValid() throws Exception {
    String maxBirthdate = "1970-01-01";
    String minBirthdate = "1965-01-01";
    mockMvc.perform(get(V1 + USERS_URL).accept(APPLICATION_JSON)
        .param("minBirthdate", minBirthdate)
        .param("maxBirthdate", maxBirthdate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].email", is(EMAIL)))
        .andExpect(jsonPath("$.content[0].lastName", is(LAST_NAME)))
        .andExpect(jsonPath("$.content[0].firstName", is(FIRST_NAME)))
        .andExpect(jsonPath("$.content[0].birthdate", is(BIRTHDATE)))
        .andExpect(jsonPath("$.content[0].address", is(ADDRESS)))
        .andExpect(jsonPath("$.content[0].phoneNumber", is(PHONE_NUMBER)));
  }

  @Test
  void createUser_shouldReturnStatus201_whenRequested() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);

    mockMvc.perform(post(V1 + USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string(LOCATION_HEADER_FIELD, containsString(USERS_URL)));
  }
}
