package com.movies_series_watch.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
	@NotBlank String refreshToken
) {
}
