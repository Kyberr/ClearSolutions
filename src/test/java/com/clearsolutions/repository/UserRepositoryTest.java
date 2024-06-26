package com.clearsolutions.repository;

import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.specification.SearchFilter;
import com.clearsolutions.service.specification.UserSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(scripts = "/db/users-data.sql")
public class UserRepositoryTest {

  private static final String EXISTING_EMAIL = "email@com";
  private static final String NOTE_EXISTING_EMAIL = "notExisted@com";
  private static final LocalDate MAX_BIRTHDATE = LocalDate.of(1970, 1, 1);
  private static final LocalDate MIN_BIRTHDATE = LocalDate.of(1965, 1, 1);

  @Autowired
  private UserRepository userRepository;

  @Test
  void findAll_shouldReturnUsersHavingBirthdateInsidePeriod_whenSearchFilterContainsMinAndMaxBirthdate() {
    Pageable pageable = Pageable.ofSize(10);
    
    SearchFilter searchFilter = SearchFilter.builder()
        .minBirthdate(MIN_BIRTHDATE)
        .maxBirthdate(MAX_BIRTHDATE)
        .build();
    Specification<User> specification = UserSpecification.getSpecification(searchFilter);
    Page<User> page = userRepository.findAll(specification, pageable);

    int expectedUsersNumber = 1;
    assertEquals(expectedUsersNumber, page.getContent().size());
  }

  @Test
  void findAll_shouldReturnUsersHavingBirthdateAfterSpecifiedDate_whenSearchFilterContainsMinBirthdate() {
    Pageable pageable = Pageable.ofSize(10);
    SearchFilter searchFilter = SearchFilter.builder().minBirthdate(MAX_BIRTHDATE).build();
    Specification<User> specification = UserSpecification.getSpecification(searchFilter);
    Page<User> page = userRepository.findAll(specification, pageable);

    assertFalse(page.getContent().isEmpty());
  }

  @Test
  void findAll_shouldReturnUsersBornBeforeSpecifiedDate_whenSearchFilterContainsMaxBirthdate() {
    Pageable pageable = Pageable.ofSize(10);
    SearchFilter searchFilter = SearchFilter.builder().maxBirthdate(MAX_BIRTHDATE).build();
    Specification<User> specification = UserSpecification.getSpecification(searchFilter);
    Page<User> page = userRepository.findAll(specification, pageable);

    assertFalse(page.getContent().isEmpty());
  }

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
