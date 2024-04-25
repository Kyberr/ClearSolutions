package com.clearsolutions.repository.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDate;
import java.util.UUID;

@StaticMetamodel(User.class)
public class User_ {

  public static volatile SingularAttribute<User, UUID> id;
  public static volatile SingularAttribute<User, String> email;
  public static volatile SingularAttribute<User, String> firstName;
  public static volatile SingularAttribute<User, String> lastName;
  public static volatile SingularAttribute<User, LocalDate> birthdate;
  public static volatile SingularAttribute<User, String> address;
  public static volatile SingularAttribute<User, String> phoneNumber;
}
