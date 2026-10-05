package com.example.jpa_workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
@AutoConfigurationPackage(basePackages = "com.example")
public class JpaWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaWorkshopApplication.class, args);
	}

}
