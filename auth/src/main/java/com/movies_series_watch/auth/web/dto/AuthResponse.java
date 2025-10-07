package com.movies_series_watch.auth.web.dto;

public record AuthResponse(
	String tokenType,
	String accessToken,
	String refreshToken
) {
}
