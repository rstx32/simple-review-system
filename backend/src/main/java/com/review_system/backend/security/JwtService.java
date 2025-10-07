package com.review_system.backend.security;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.review_system.backend.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	private final Key signingKey;

	public JwtService(JwtProperties properties) {
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
	}

	public Optional<Claims> parse(String token) {
		try {
			Jws<Claims> jws = Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.build()
				.parseClaimsJws(token);
			return Optional.ofNullable(jws.getBody());
		} catch (RuntimeException ex) {
			return Optional.empty();
		}
	}

	public boolean isValid(Claims claims) {
		Date expiration = claims.getExpiration();
		return expiration == null || expiration.after(new Date());
	}

	public Optional<UUID> extractUserId(Claims claims) {
		Object uid = Optional.ofNullable(claims.get("uid")).orElse(claims.get("user_id"));
		if (uid instanceof String value) {
			try {
				return Optional.of(UUID.fromString(value));
			} catch (IllegalArgumentException ex) {
				return Optional.empty();
			}
		}
		String subject = claims.getSubject();
		if (subject != null) {
			try {
				return Optional.of(UUID.fromString(subject));
			} catch (IllegalArgumentException ignored) {
			}
		}
		return Optional.empty();
	}

	public String extractEmail(Claims claims) {
		Object email = claims.get("email");
		if (email instanceof String value) {
			return value;
		}
		return claims.getSubject();
	}
}
