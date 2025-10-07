package com.movies_series_watch.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.movies_series_watch.auth.web.validation.RoleType;

public record RegisterRequest(
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8, max = 128) String password,
		@Size(max = 100) String displayName,
		@NotBlank @RoleType String role) {
}
