package com.review_system.backend.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

	public Optional<UserPrincipal> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
			return Optional.empty();
		}
		return Optional.of(principal);
	}

	public UUID requireUserId() {
		return getCurrentUser()
			.map(UserPrincipal::userId)
			.orElseThrow(() -> new IllegalStateException("User not authenticated"));
	}

	public String requireEmail() {
		return getCurrentUser()
			.map(user -> user.email() != null ? user.email() : user.userId().toString())
			.orElseThrow(() -> new IllegalStateException("User not authenticated"));
	}
}
