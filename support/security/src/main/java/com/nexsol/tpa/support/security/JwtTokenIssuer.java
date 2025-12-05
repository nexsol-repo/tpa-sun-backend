package com.nexsol.tpa.support.security;

import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.JwtPayload;
import com.nexsol.tpa.core.domain.TokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenIssuer implements TokenIssuer {

	private final JwtProperties jwtProperties;

	private final SecretKey key;

	public JwtTokenIssuer(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		// 1. 생성자에서 SecretKey를 딱 한 번만 만듭니다. (Base64 디코딩)
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	@Override
	public AuthToken issue(Long userId, String email) {
		Instant now = Instant.now();

		String accessToken = createToken(userId, email, now, jwtProperties.getAccessTokenExpiration());
		String refreshToken = createToken(userId, email, now, jwtProperties.getRefreshTokenExpiration());

		return AuthToken.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
			.refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
			.build();
	}

	private String createToken(Long userId, String email, Instant now, long expirationSeconds) {
		return Jwts.builder()
			.subject(String.valueOf(userId))
			.claim("email", email)
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(expirationSeconds)))
			.signWith(this.key, Jwts.SIG.HS256)
			.compact();
	}

	public JwtPayload parseToken(String token) {

		Claims claims = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();

		Long sub = Long.parseLong(claims.getSubject());
		return new JwtPayload(sub);
	}

	public boolean validateToken(String token) {
		try {

			Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token);
			return true;
		}
		catch (JwtException | IllegalArgumentException e) {
			log.error("토큰 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public Long getUserId(String token) {
		return parseToken(token).sub();
	}

}
