package com.clearsolutions;

import com.clearsolutions.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class ClearSolutionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClearSolutionsApplication.class, args);
	}

}
