package com.clearsolutions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter
@Setter
public class AppConfig {

  private int minimalAgeInYears;
}
