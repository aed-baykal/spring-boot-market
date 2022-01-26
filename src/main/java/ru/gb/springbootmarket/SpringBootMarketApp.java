package ru.gb.springbootmarket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SpringBootMarketApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMarketApp.class, args);
	}
}
