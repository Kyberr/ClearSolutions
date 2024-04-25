package com.clearsolutions.controller;

import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.util.TestDataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/db/users-data.sql")
@Transactional
public class UserControllerIntegrationTest {

  private static final String USERS_URL = "/v1/users";
  private static final String LOCATION_HEADER_FIELD = "Location";
  private static final String EMAIL = "email@com";
  private static final String FIRST_NAME = "Linus";
  private static final String LAST_NAME = "Torvalds";
  private static final String BIRTHDATE = "1969-12-28";
  private static final String ADDRESS = "some address";
  private static final String PHONE_NUMBER = "+38(097)-100-00-00";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void search_shouldReturnStatus200AndBody_whenRequested() throws Exception {
    String maxBirthdate = "1970-01-01";
    String minBirthdate = "1965-01-01";
    mockMvc.perform(get(USERS_URL).accept(APPLICATION_JSON)
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

    mockMvc.perform(post(USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string(LOCATION_HEADER_FIELD, containsString(USERS_URL)));
  }
}
