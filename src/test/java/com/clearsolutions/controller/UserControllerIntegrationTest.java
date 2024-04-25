package com.clearsolutions.controller;

import com.clearsolutions.service.dto.UserDto;
import com.clearsolutions.util.TestDataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

  private static final String USERS_URL = "/v1/users";
  private static final String LOCATION_HEADER_FIELD = "Location";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createUser_shouldReturnStatus201_whenRequested() throws Exception {
    UserDto userDto = TestDataGenerator.generateUserDto();
    String requestBody = objectMapper.writeValueAsString(userDto);

    mockMvc.perform(post(USERS_URL).contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string(LOCATION_HEADER_FIELD, containsString(USERS_URL)));
  }
}
