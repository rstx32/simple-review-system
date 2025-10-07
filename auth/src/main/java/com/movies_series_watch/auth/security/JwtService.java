package com.movies_series_watch.auth.security;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.movies_series_watch.auth.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String TOKEN_TYPE_CLAIM = "token_type";

	private final JwtProperties properties;

	public JwtService(JwtProperties properties) {
		this.properties = properties;
	}

	public String generateAccessToken(UserDetails userDetails) {
		return generateAccessToken(userDetails, Map.of());
	}

	public String generateAccessToken(UserDetails userDetails, Map<String, Object> extraClaims) {
		return buildToken(extraClaims, userDetails, properties.accessTokenExpiration(), TokenType.ACCESS);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return generateRefreshToken(userDetails, Map.of());
	}

	public String generateRefreshToken(UserDetails userDetails, Map<String, Object> extraClaims) {
		return buildToken(extraClaims, userDetails, properties.refreshTokenExpiration(), TokenType.REFRESH);
	}

	public boolean isTokenValid(String token, UserDetails userDetails, TokenType expectedType) {
		String username = extractUsername(token);
		TokenType tokenType = extractTokenType(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && tokenType == expectedType;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Duration expiration, TokenType tokenType) {
		Instant now = Instant.now();
		Instant expiry = now.plus(expiration);

		return Jwts.builder()
			.setClaims(extraClaims)
			.setSubject(userDetails.getUsername())
			.claim(TOKEN_TYPE_CLAIM, tokenType.name())
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiry))
			.signWith(getSigningKey())
			.compact();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private TokenType extractTokenType(String token) {
		String rawType = extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
		if (rawType == null) {
			throw new IllegalStateException("Token type claim missing");
		}
		try {
			return TokenType.valueOf(rawType);
		} catch (IllegalArgumentException ex) {
			throw new IllegalStateException("Unknown token type: " + rawType, ex);
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}