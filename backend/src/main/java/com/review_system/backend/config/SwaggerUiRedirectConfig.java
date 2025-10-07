package com.review_system.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerUiRedirectConfig {

	@GetMapping("/swagger-ui")
	public String redirectSwaggerUi() {
		return "forward:/swagger-ui/index.html";
	}
}
