package com.movies_series_watch.auth.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_series_watch.auth.service.AuthService;
import com.movies_series_watch.auth.web.dto.AuthResponse;
import com.movies_series_watch.auth.web.dto.LoginRequest;
import com.movies_series_watch.auth.web.dto.RefreshTokenRequest;
import com.movies_series_watch.auth.web.dto.RegisterRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		AuthResponse response = authService.refresh(request);
		return ResponseEntity.ok(response);
	}
}
