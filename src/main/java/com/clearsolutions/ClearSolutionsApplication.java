package com.clearsolutions;

import com.clearsolutions.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@PropertySource("classpath:/config.properties")
public class ClearSolutionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClearSolutionsApplication.class, args);
	}

}
