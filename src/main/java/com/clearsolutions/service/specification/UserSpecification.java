package com.clearsolutions.service.specification;

import com.clearsolutions.repository.entity.User;
import com.clearsolutions.repository.entity.User_;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class UserSpecification {

  public static Specification<User> getSpecification(SearchFilter searchFilter) {
    return (userRoot, userQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (nonNull(searchFilter.getMaxBirthdate())) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(userRoot.get(User_.birthdate),
                                                         searchFilter.getMaxBirthdate()));
      }

      if (nonNull(searchFilter.getMinBirthdate())) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(userRoot.get(User_.birthdate),
                                                            searchFilter.getMinBirthdate()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
