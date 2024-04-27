package com.clearsolutions.service.utils;

import com.clearsolutions.TestDataGenerator;
import com.clearsolutions.service.dto.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperUtilsTest {

  private static final int PROPERTIES_QUANTITY_WITH_NULL = 1;
  private static final String PROPERTY_NAME = "id";

  @Test
  void definePropertiesWithNullValues_shouldReturnPropertyNames_whenPropertyIsNull() {
    UserDto user = TestDataGenerator.generateUserDto();
    String[] properties = MapperUtils.definePropertiesWithNullValues(user);

    assertEquals(PROPERTIES_QUANTITY_WITH_NULL, properties.length);
    assertEquals(PROPERTY_NAME, properties[0]);
  }
}
