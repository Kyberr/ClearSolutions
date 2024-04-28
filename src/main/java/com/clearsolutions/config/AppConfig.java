package com.clearsolutions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Sort.Direction;

@ConfigurationProperties
@Getter
@Setter
public class AppConfig {

  private int minimalAgeInYears;
  private String userSortBy;
  private Direction userSortDirection;
}
