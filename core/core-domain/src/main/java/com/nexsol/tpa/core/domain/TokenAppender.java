package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenAppender {

	private final RefreshTokenRepository refreshTokenRepository;

	public void append(RefreshToken refreshToken) {
		refreshTokenRepository.save(refreshToken);
	}

}
