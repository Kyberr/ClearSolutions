package com.clearsolutions.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(scripts = "/db/users-data.sql")
public class UserRepositoryTest {

  private static final String EXISTING_EMAIL = "email@com";
  private static final String NOTE_EXISTING_EMAIL = "notExisted@com";

  @Autowired
  private UserRepository userRepository;

  @Test
  void existsByEmail_shouldReturnFalse_whenNoUserWithSuchEmailInDb() {
    boolean isExist = userRepository.existsByEmail(NOTE_EXISTING_EMAIL);

    assertFalse(isExist);
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenUserWithSuchEmailIsInDb() {
    boolean isExist = userRepository.existsByEmail(EXISTING_EMAIL);

    assertTrue(isExist);
  }
}
