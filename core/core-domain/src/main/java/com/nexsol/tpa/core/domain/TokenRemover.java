package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenRemover {

	private final RefreshTokenRepository refreshTokenRepository;

	public void remove(String token) {
		refreshTokenRepository.deleteByToken(token);
	}

}
