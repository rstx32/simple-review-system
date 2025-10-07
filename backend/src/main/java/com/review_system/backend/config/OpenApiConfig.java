package com.review_system.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI reviewSystemOpenApi() {
		return new OpenAPI()
				.components(new Components())
				.info(new Info()
						.title("Review System API")
						.description("API documentation for the Review System application.")
						.version("v1.0.0")
						.license(new License().name("MIT License")));
	}
}
