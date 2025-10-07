package com.review_system.backend.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(7);
		Optional<Claims> claimsOpt = jwtService.parse(token);
		if (claimsOpt.isEmpty() || !jwtService.isValid(claimsOpt.get())) {
			filterChain.doFilter(request, response);
			return;
		}

		Claims claims = claimsOpt.get();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<UUID> userIdOpt = jwtService.extractUserId(claims);
			if (userIdOpt.isPresent()) {
				String email = jwtService.extractEmail(claims);
				UserPrincipal principal = new UserPrincipal(userIdOpt.get(), email);
				Collection<SimpleGrantedAuthority> authorities = extractAuthorities(claims);
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(principal, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(request, response);
	}

	@SuppressWarnings("unchecked")
	private Collection<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
		Object raw = claims.get("roles");
		if (raw instanceof List<?> list) {
			return list.stream()
				.filter(String.class::isInstance)
				.map(String.class::cast)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		}
		if (raw instanceof String role) {
			return List.of(new SimpleGrantedAuthority(role));
		}
		return List.of();
	}
}
