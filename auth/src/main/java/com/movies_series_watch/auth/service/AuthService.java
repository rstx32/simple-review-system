package com.movies_series_watch.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.movies_series_watch.auth.security.JwtService;
import com.movies_series_watch.auth.security.TokenType;
import com.movies_series_watch.auth.user.Role;
import com.movies_series_watch.auth.user.RoleRepository;
import com.movies_series_watch.auth.user.User;
import com.movies_series_watch.auth.user.UserRepository;
import com.movies_series_watch.auth.web.dto.AuthResponse;
import com.movies_series_watch.auth.web.dto.LoginRequest;
import com.movies_series_watch.auth.web.dto.RefreshTokenRequest;
import com.movies_series_watch.auth.web.dto.RegisterRequest;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
		}

		if (request.role() == null || request.role().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
		}

		User user = new User();
		user.setEmail(request.email());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setDisplayName(request.displayName());

		Role userRole = roleRepository.findByName(request.role())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role"));
		user.addRole(userRole);

		User saved = userRepository.save(user);

		return buildAuthResponse(saved);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		return buildAuthResponse(user);
	}

	@Transactional(readOnly = true)
	public AuthResponse refresh(RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();
		String email;
		try {
			email = jwtService.extractUsername(refreshToken);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

		boolean validRefresh;
		try {
			validRefresh = jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}

		if (!validRefresh) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}

		return buildAuthResponse(user);
	}

	private AuthResponse buildAuthResponse(User user) {
		Map<String, Object> claims = buildTokenClaims(user);
		String accessToken = jwtService.generateAccessToken(user, claims);
		String refreshToken = jwtService.generateRefreshToken(user, new HashMap<>(claims));
		return new AuthResponse("Bearer", accessToken, refreshToken);
	}

	private Map<String, Object> buildTokenClaims(User user) {
		Map<String, Object> claims = new HashMap<>();
		if (user.getId() != null) {
			claims.put("uid", user.getId().toString());
		}
		claims.put("email", user.getEmail());
		List<String> roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		claims.put("roles", roles);
		return claims;
	}
}
