package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RefreshToken(String token, Long userId, LocalDateTime issuedAt, LocalDateTime expiredAt) {

	public static RefreshToken create(Long userId, String token, long expirationSeconds) {
		return RefreshToken.builder()
			.userId(userId)
			.token(token)
			.expiredAt(LocalDateTime.now().plusSeconds(expirationSeconds))
			.build();
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiredAt);
	}
}
